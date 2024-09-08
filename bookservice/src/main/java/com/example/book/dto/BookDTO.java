package com.example.book.dto;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class BookDTO {

    private Integer id;
    private String name;
    private String author;
    private String publisher;
    private Double price;
}