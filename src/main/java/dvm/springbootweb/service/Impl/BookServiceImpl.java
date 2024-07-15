package dvm.springbootweb.service.Impl;

import dvm.springbootweb.entity.Book;
import dvm.springbootweb.repository.BookRepository;
import dvm.springbootweb.service.BookService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * BookServiceImpl class implements BookService interface
 * This class is used to implement all the methods in BookService interface
 * This class is used to interact with the database
 */
@Service
public class BookServiceImpl implements BookService {
    @Autowired
    private BookRepository bookRepository;
    private static final Logger LOGGER = LogManager.getLogger(BookServiceImpl.class);
    @Override
    public List<Book> findBooks(int limit) {
        return  bookRepository.findBooks(PageRequest.of(0,limit));
    }

    @Override
    public Set<Book> findAllByCategoryId(int id) {
        return bookRepository.findAllByCategoryId(id);
    }

    @Override
    public Book findByBookId(Integer id) {
        return bookRepository.findAllByBookId(id);
    }

    @Override
    public Page<Book> getPagging(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }


    @Override
    public void saveOrUpdate(Book book) {
        bookRepository.save(book);
    }

    /**
     * delete book by id
     * @param id
     */
    @Override
    public void delete(Integer id) {
        Book book = bookRepository.findById(id).get();
        if (book == null){
            LOGGER.error("No book has id:"+id);
            throw new RuntimeException("No book has id:"+id);
        }
        bookRepository.deleteById(id);
    }

    /**
     * validate book
     */
    @Override
    public String validateBook(String bookName, String author, String description, double price, int categoryId, int quantity) {
        if(bookName == null || bookName.isEmpty()){
            return "Book name is required!";
        }
        if(author == null || author.isEmpty()){
            return "Author is required!";
        }
        if(description == null || description.isEmpty()){
            return "Description is required!";
        }
        if(price == 0){
            return "Price is required!";
        }
        if(categoryId == 0){
            return "Category is required!";
        }
        if(quantity == 0){
            return "Quantity is required!";
        }
        return "1";
    }
}

