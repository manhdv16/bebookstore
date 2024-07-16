package com.dvm.bookstore.repository;

import com.dvm.bookstore.entity.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {
    Category findByCategoryId(int id);
    @Query("select count(b) from Book b where b.category.categoryId = ?1")
    int existBookByCategoryId(int id);

    @Query("select c from Category c")
    List<Category> findCategories(Pageable pageable);
}
