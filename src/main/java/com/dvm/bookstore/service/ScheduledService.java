package com.dvm.bookstore.service;

import com.dvm.bookstore.entity.Book;
import com.dvm.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduledService {
    private final BookRepository bookRepository;

    public void updateBookStatus() {
        List<Book> books = bookRepository.findAll();
        for (Book b: books) {
            b.setStatus(1);
            bookRepository.save(b);
        }
        System.out.println("-------- DONE JOB -------");
    }
}
