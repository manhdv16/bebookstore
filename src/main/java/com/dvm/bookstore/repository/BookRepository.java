package com.dvm.bookstore.repository;

import com.dvm.bookstore.dto.response.BookDetailResponse;
import com.dvm.bookstore.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
@Repository
public interface BookRepository extends JpaRepository<Book,Integer> {
    @Query("select b.bookId,b.bookName,b.author,b.description,b.image,b.price,b.quantity,b.sold,b.category.categoryName from Book b where b.bookId = :id")
    Optional<Book> findByBookId(Integer id);
    @Query("select b.bookId,b.bookName,b.author,b.description,b.image,b.price,b.quantity,b.sold,b.category.categoryName  from Book b where b.category.categoryId = :categoryId")
    Set<Book> findAllByCategoryId(int categoryId);
    @Query("select new com.dvm.bookstore.dto.response.BookDetailResponse(b.bookId,b.bookName,b.description,b.author,b.image,b.price,b.quantity,b.sold,b.category.categoryName) from Book b order by b.sold desc")
    List<BookDetailResponse> findBooks(Pageable page);

    @Query("select new com.dvm.bookstore.dto.response.BookDetailResponse(b.bookId,b.bookName,b.description,b.author,b.image,b.price,b.quantity,b.sold,b.category.categoryName) from Book b")
    Page<BookDetailResponse> getBookByPaging(Pageable pageable);
}