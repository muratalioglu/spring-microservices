package com.example.book.service;

import com.example.book.dto.BookDTO;
import com.example.book.dto.BookInDTO;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.function.SupplierUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

@Service
public class BookServiceImpl implements BookService {

    final Map<Integer, BookDTO> bookRepositoryTR = new HashMap<>();
    final Map<Integer, BookDTO> bookRepositoryEN = new HashMap<>();

    @Autowired
    PriceRestTemplateClient priceRestTemplateClient;

    @Autowired
    BulkheadRegistry bulkheadRegistry;

    @Autowired
    CircuitBreakerRegistry circuitBreakerRegistry;

    @PostConstruct
    private void loadBooks() {
        bookRepositoryTR.putAll(
                Map.of(
                        1,
                        new BookDTO(
                                1,
                                "Modern Türkiye'nin Doğuşu",
                                "Bernard Lewis",
                                "Arkadaş Yayınevi",
                                null
                        ),
                        2,
                        new BookDTO(
                                2,
                                "Saatleri Ayarlama Enstitüsü",
                                "Ahmet Hamdi Tanpınar",
                                "Dergah Yayınları",
                                null
                        )
                )
        );

        Map.of(
                1,
                new BookDTO(
                        1,
                        "The Emergence of Modern Turkey",
                        "Bernard Lewis",
                        "Arkadaş",
                        null
                ),
                2,
                new BookDTO(
                        2,
                        "The Time Regulation Institute",
                        "Ahmet Hamdi Tanpınar",
                        "Dergah",
                        null
                )
        );
    }

    @Override
    public BookDTO getBook(Integer id, String clientType, String language) {

        BookDTO bookDTO =
                language.equals("tr") ?
                        bookRepositoryTR.get(id) :
                        bookRepositoryEN.get(id);

        if (bookDTO == null)
            return null;

        Double price = getPriceBulkhead(bookDTO);

        bookDTO.setPrice(price);

        return bookDTO;
    }

    public Double getPrice(BookDTO bookDTO) {

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("priceServiceCircuitBreaker");

        Supplier<Double> supplier =
                CircuitBreaker.decorateSupplier(
                        circuitBreaker,
                        () -> priceRestTemplateClient.getPrice(bookDTO.getId())
                );

        return circuitBreaker.executeSupplier(supplier);
    }

    public Double getPriceBulkhead(BookDTO bookDTO) {

        Bulkhead bulkhead = bulkheadRegistry.bulkhead("priceServiceBulkhead");

        Supplier<Double> supplier =
                Bulkhead.decorateSupplier(
                        bulkhead,
                        () -> priceRestTemplateClient.getPrice(bookDTO.getId())
                );

        return bulkhead.executeSupplier(supplier);
    }

    @Override
    public void createBook(BookInDTO bookInDTO, String language) {

        int id;
        if (language.equals("tr")) {
            id = Collections.max(bookRepositoryTR.keySet()) + 1;
            bookRepositoryTR.put(
                    id,
                    new BookDTO(
                            id,
                            bookInDTO.getName(),
                            bookInDTO.getAuthor(),
                            bookInDTO.getPublisher(),
                            null
                    )
            );
        } else {
            id = Collections.max(bookRepositoryEN.keySet()) + 1;
            bookRepositoryEN.put(
                    id,
                    new BookDTO(
                            id,
                            bookInDTO.getName(),
                            bookInDTO.getAuthor(),
                            bookInDTO.getPublisher(),
                            null
                    )
            );
        }
    }
}