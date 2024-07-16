package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.entity.Book;
import com.dvm.bookstore.entity.Category;
import com.dvm.bookstore.repository.CategoryRepository;
import com.dvm.bookstore.service.BookService;
import com.dvm.bookstore.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * CategoryServiceImpl class implements CategoryService interface
 * @see CategoryService
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BookService bookService;

    @Override
    public List<Category> findCategories(int limit) {
        return categoryRepository.findCategories(PageRequest.of(0,3));
    }

    @Override
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Override
    public Category findById(int id) {
        return categoryRepository.findByCategoryId(id);
    }

    @Override
    public Set<Book> getAllBooks(int id) {
        return bookService.findAllByCategoryId(id);
    }

    @Override
    public void save(Category category) {
        categoryRepository.save(category);
    }

    @Override
    public void delete(int id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public boolean checkBookExist(int id) {
        return categoryRepository.existBookByCategoryId(id)>0;
    }
}
