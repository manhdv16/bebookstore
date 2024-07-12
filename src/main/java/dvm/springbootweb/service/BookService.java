package dvm.springbootweb.service;

import dvm.springbootweb.dto.BookDto;
import dvm.springbootweb.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface BookService {
    List<Book> findBooks(int limit);
    Set<Book> findAllByCategoryId(int id);
    Book findByBookId(Integer id);
    Page<Book> getPagging(Pageable pageable);
    void saveOrUpdate(Book book);
    void delete(Integer id);
    String validateBook(String bookName, String author, String description, double price, int categoryId, int quantity);
}
