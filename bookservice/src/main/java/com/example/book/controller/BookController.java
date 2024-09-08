package com.example.book.controller;

import com.example.book.config.DiscountCoupon;
import com.example.book.dto.BookDTO;
import com.example.book.dto.BookInDTO;
import com.example.book.service.BookService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/books")
@RefreshScope
public class BookController {

    private final BookService bookService;

    private final DiscountCoupon discountCoupon;

    public BookController(BookService bookService,
                          DiscountCoupon discountCoupon) {
        this.bookService = bookService;
        this.discountCoupon = discountCoupon;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBook(@PathVariable Integer id,
                                     @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = "tr") String language) {

        BookDTO bookDTO = bookService.getBook(id, "", language);

        if (bookDTO == null)
            return ResponseEntity.noContent().build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("DiscountCouponCode", discountCoupon.getCode())
                .header("DiscountCouponPassword", discountCoupon.getPassword())
                .body(bookDTO);
    }

    @GetMapping("/{id}/{clientType}")
    public ResponseEntity<?> getBookWithClient(@PathVariable Integer id,
                                               @PathVariable String clientType,
                                               @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = "tr") String language) {

        BookDTO bookDTO = bookService.getBook(id, clientType, language);

        if (bookDTO == null)
            return ResponseEntity.noContent().build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .header("DiscountCouponCode", discountCoupon.getCode())
                .header("DiscountCouponPassword", discountCoupon.getPassword())
                .body(bookDTO);
    }

    @PostMapping
    public ResponseEntity<?> createBook(@RequestBody BookInDTO bookInDTO,
                                        @RequestHeader(value = HttpHeaders.ACCEPT_LANGUAGE, defaultValue = "tr") String language) {

        bookService.createBook(bookInDTO, language);

        return ResponseEntity.noContent().build();
    }
}