package com.example.book.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class BookInDTO {

    private String name;
    private String author;
    private String publisher;
}