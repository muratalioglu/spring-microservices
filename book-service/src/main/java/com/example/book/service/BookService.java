package com.example.book.service;

import com.example.book.dto.BookDTO;
import com.example.book.dto.BookInDTO;

public interface BookService {

    BookDTO getBook(Integer id, String clientType, String language);

    void createBook(BookInDTO bookInDTO, String language);
}