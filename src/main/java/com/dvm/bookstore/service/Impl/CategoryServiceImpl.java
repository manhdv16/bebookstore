package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.dto.request.CategoryDto;
import com.dvm.bookstore.dto.response.CategoryResponse;
import com.dvm.bookstore.entity.Book;
import com.dvm.bookstore.entity.Category;
import com.dvm.bookstore.dto.response.PageResponse;
import com.dvm.bookstore.exception.AppException;
import com.dvm.bookstore.exception.ErrorCode;
import com.dvm.bookstore.repository.CategoryRepository;
import com.dvm.bookstore.service.BookService;
import com.dvm.bookstore.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * CategoryServiceImpl class implements CategoryService interface
 * @see CategoryService
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final BookService bookService;
    private static final Logger LOGGER = LogManager.getLogger(CategoryServiceImpl.class);

    @Override
    public List<Category> findCategories(int limit) {
        return categoryRepository.findCategories(PageRequest.of(0,3));
    }

    @Override
    public List<CategoryResponse> findAll() {
        return categoryRepository.findAllCategory();
    }

    @Override
    public Category findById(int id) {
        return categoryRepository.findById(id).orElseThrow(()-> new AppException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    @Override
    public Set<Book> getAllBooks(int id) {
        return bookService.findAllByCategoryId(id);
    }

    @Override
    public void addCategory(CategoryDto dto) {
        if(categoryRepository.existsCategoryByCategoryName(dto.getCategoryName())) {
            throw new AppException(ErrorCode.CATEGORY_NAME_EXISTS);
        }
        Category category = Category.builder()
                .categoryName(dto.getCategoryName())
                .description(dto.getDescription()).build();
        categoryRepository.save(category);
    }

    @Override
    public void updateCategory(Category category) {
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

    /**
     * Get categories by advance search with specifications
     * @param pageNo
     * @param pageSize
     * @param sorts
     * @param search
     * @return
     */
    @Override
    public PageResponse<?> advanceSearchWithSpecifications(int pageNo, int pageSize, String[] sorts, String[] search) {
        LOGGER.info("get categories by specifications");
        Specification<Category> nameSpec = Specification.where(((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("categoryName"),"T%")));
        Specification<Category> desSpec = Specification.where(((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("description"),"%cổ tích%")));
        Specification<Category> spec = nameSpec.and(desSpec);
        List<Category> categories = categoryRepository.findAll(spec);
        return PageResponse.builder()
                .items(categories).build();
    }
}
