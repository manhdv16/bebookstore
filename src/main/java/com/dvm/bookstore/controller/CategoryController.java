package com.dvm.bookstore.controller;

import com.dvm.bookstore.dto.request.CategoryDto;
import com.dvm.bookstore.dto.request.CategoryRequest;
import com.dvm.bookstore.dto.response.APIResponse;
import com.dvm.bookstore.dto.response.MessageResponse;
import com.dvm.bookstore.dto.response.PageResponse;
import com.dvm.bookstore.entity.Book;
import com.dvm.bookstore.service.BookService;
import com.dvm.bookstore.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Category Controller
 */
@CrossOrigin("*")
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final BookService bookService;
    private static final Logger LOGGER = LogManager.getLogger(CategoryController.class);

    /**
     * Get all categories
     * @return categories
     */
    @GetMapping("/categories")
    public APIResponse<?> getAll() {
        return APIResponse.builder()
                .code(200)
                .message("Get all categories successfully")
                .data(categoryService.findAll()).build();
    }

    /**
     * Get categories with advance search by specifications
     * @param pageNo
     * @param pageSize
     * @param sorts
     * @param search
     * @return list categories
     */
    @GetMapping("/advance-search-with-specifications")
    public APIResponse<?> advanceSearchWithSpecification(
            @RequestParam(defaultValue = "0",required = false) int pageNo,
            @RequestParam(defaultValue = "10",required = false) int pageSize,
            @RequestParam(required = false) String[] sorts,
            @RequestParam(required = false) String[] search) {
        PageResponse<?> categories = categoryService.advanceSearchWithSpecifications(pageNo,pageSize,sorts,search);
        return APIResponse.builder()
                .code(200)
                .message("Get all categories successfully")
                .data(categories).build();
    }
    /**
     * Get all books of category
     * @param id
     * @return books
     */
    @GetMapping("/booksOfCategory/{id}")
    public APIResponse<?> getAllBooks(@PathVariable int id){
        Set<Book> listBooks = bookService.findAllByCategoryId(id);
        return APIResponse.builder()
                .code(200)
                .message("Get all books of category successfully")
                .data(listBooks).build();
    }
    /**
     * Add category
     * @param dto
     * @return message
     */
    @PostMapping("/addCategory")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public APIResponse<?> addCategory(@Valid @RequestBody CategoryDto dto) {
        categoryService.addCategory(dto);
        return APIResponse.builder()
                .code(200)
                .message("added successfully")
                .build();
    }
    /**
     * Update category
     * @param id
     * @param request
     * @return message
     */
    @PutMapping("/updateCategory/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')")
    public APIResponse<?> updateCategory(@PathVariable int id, CategoryRequest request) {
        categoryService.updateCategory(id, request);
        return APIResponse.builder()
                .code(200)
                .message("updated successfully")
                .build();
    }
    /**
     * Delete category
     * @param id
     * @return message
     */
    @DeleteMapping("/deleteCategory/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public APIResponse<?> delete(@PathVariable int id) {
        categoryService.delete(id);
        return APIResponse.builder()
                .code(200)
                .message("deleted successfully")
                .build();
    }
}
