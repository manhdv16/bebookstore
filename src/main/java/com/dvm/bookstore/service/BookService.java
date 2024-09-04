package com.dvm.bookstore.service;

import com.dvm.bookstore.dto.request.BookCreationRequest;
import com.dvm.bookstore.dto.response.BookDetailResponse;
import com.dvm.bookstore.entity.Book;
import com.dvm.bookstore.dto.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface BookService {
    List<BookDetailResponse> findBooks(int page, int size);
    Set<Book> findAllByCategoryId(int id);
    Book findByBookId(Integer id);
    Page<BookDetailResponse> getPagging(Pageable pageable);
    void updateBook(int bookId, BookCreationRequest request);
    void addBook(BookCreationRequest request);
    void save(Book book);
    void delete(Integer id);
    PageResponse<?> getAllBooksWithSortByMultipleField(int pageNo, int pageSize, String... sorts);
    PageResponse<?> getListBookBySearchPagingAndSorting(int pageNo,int pageSize,String search,String sort);
    PageResponse<?> getListBookWithAdvanceSearchByCriteria(int pageNo, int pageSize, LocalDate cmtDate, String sortBy, String... search);
}
