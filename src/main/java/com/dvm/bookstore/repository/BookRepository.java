package com.dvm.bookstore.repository;

import com.dvm.bookstore.entity.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Repository
public interface BookRepository extends JpaRepository<Book,Integer> {
    Optional<Book> findByBookId(Integer id);
    Set<Book> findAllByCategoryId(int categoryId);
    @Query("select b from Book b order by b.sold desc")
    List<Book> findBooks(Pageable page);
}