package com.dvm.bookstore.service;

import com.dvm.bookstore.dto.request.CategoryDto;
import com.dvm.bookstore.dto.request.CategoryRequest;
import com.dvm.bookstore.dto.response.CategoryResponse;
import com.dvm.bookstore.entity.Category;
import com.dvm.bookstore.entity.Book;
import com.dvm.bookstore.dto.response.PageResponse;

import java.util.List;
import java.util.Set;

public interface CategoryService {
    List<Category> findCategories(int limit);
    List<CategoryResponse> findAll();
    Category findById(int id);
    void addCategory(CategoryDto categoryDto);
    void updateCategory(int id, CategoryRequest request);
    void delete(int id);
    boolean checkBookExist(int id);
    PageResponse<?> advanceSearchWithSpecifications(int pageNo, int pageSize, String[] sorts, String[] search);
}
