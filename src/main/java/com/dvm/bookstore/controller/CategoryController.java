package com.dvm.bookstore.controller;

import com.dvm.bookstore.dto.request.CategoryDto;
import com.dvm.bookstore.entity.Book;
import com.dvm.bookstore.entity.Category;
import com.dvm.bookstore.dto.response.MessageResponse;
import com.dvm.bookstore.dto.response.PageResponse;
import com.dvm.bookstore.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
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
    private static final Logger LOGGER = LogManager.getLogger(CategoryController.class);

    /**
     * Get all categories
     * @return categories
     */
    @GetMapping("/categories")
    public ResponseEntity<?> getAll() {
        return new ResponseEntity<>(categoryService.findAll(), HttpStatus.OK);
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
    public ResponseEntity<?> advanceSearchWithSpecification(
            @RequestParam(defaultValue = "0",required = false) int pageNo,
            @RequestParam(defaultValue = "10",required = false) int pageSize,
            @RequestParam(required = false) String[] sorts,
            @RequestParam(required = false) String[] search) {
        PageResponse<?> categories = categoryService.advanceSearchWithSpecifications(pageNo,pageSize,sorts,search);
        return ResponseEntity.ok(categories);
    }
    /**
     * Get all books of category
     * @param id
     * @return books
     */
    @GetMapping("/booksOfCategory/{id}")
    public ResponseEntity<?> getAllBooks(@PathVariable int id){
        Set<Book> listBooks = categoryService.getAllBooks(id);
        return ResponseEntity.ok(listBooks);
    }
    /**
     * Add category
     * @param dto
     * @return message
     */
    @PostMapping("/addCategory")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryDto dto) {
        categoryService.addCategory(dto);
        return ResponseEntity.ok(new MessageResponse("added successfully"));
    }
    /**
     * Update category
     * @param id
     * @param categoryName
     * @param description
     * @return message
     */
    @PutMapping("/updateCategory/{id}")
    @PreAuthorize("hasAnyRole('ROLE_MANAGER','ROLE_ADMIN')")
    public ResponseEntity<?> updateCategory(@PathVariable int id, @RequestParam( required = false) String categoryName,
                                    @RequestParam( required = false) String description) {
        Category category = categoryService.findById(id);
        if (categoryName != null) category.setCategoryName(categoryName);
        if(description != null) category.setDescription(description);
        categoryService.updateCategory(category);
        return ResponseEntity.ok(new MessageResponse("updated successfully"));
    }
    /**
     * Delete category
     * @param id
     * @return message
     */
    @DeleteMapping("/deleteCategory/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> delete(@PathVariable int id) {
        if(!categoryService.checkBookExist(id)) {
            categoryService.delete(id);
            return ResponseEntity.ok(new MessageResponse("deleted successfully"));
        }
        LOGGER.error("book exist in category with category id: "+id);
        return ResponseEntity.badRequest().body(new MessageResponse("book exist"));
    }
}
