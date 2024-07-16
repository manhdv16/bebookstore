package com.dvm.bookstore.service;

import com.dvm.bookstore.entity.Category;
import com.dvm.bookstore.entity.Book;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    List<Category> findCategories(int limit);
    List<Category> findAll();
    Category findById(int id);
    Set<Book> getAllBooks(int id);
    void save(Category category);
    void delete(int id);
    boolean checkBookExist(int id);
}
