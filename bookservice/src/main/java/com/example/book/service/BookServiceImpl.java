package com.example.book.service;

import com.example.book.dto.BookDTO;
import com.example.book.dto.BookInDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class BookServiceImpl implements BookService {

    final Map<Integer, BookDTO> bookRepositoryTR = new HashMap<>();
    final Map<Integer, BookDTO> bookRepositoryEN = new HashMap<>();

    @Autowired
    PriceRestTemplateClient priceRestTemplateClient;

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

        Double price = getPrice(bookDTO);

        bookDTO.setPrice(price);

        return bookDTO;
    }

    public Double getPrice(BookDTO bookDTO) {
        return priceRestTemplateClient.getPrice(bookDTO.getId());
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