package com.dvm.bookstore.controller;

import com.dvm.bookstore.cloudinary.CloudinaryService;
import com.dvm.bookstore.entity.Category;
import com.dvm.bookstore.entity.Book;
import com.dvm.bookstore.entity.Comment;
import com.dvm.bookstore.payload.response.MessageResponse;
import com.dvm.bookstore.payload.response.PageResponse;
import com.dvm.bookstore.service.BookService;
import com.dvm.bookstore.service.CategoryService;
import com.dvm.bookstore.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;


/**
 * Book Controller
 */
@CrossOrigin("*")
@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;
    private final CommentService commentService;
    private static final Logger LOGGER = LogManager.getLogger(BookController.class);
    /**
     * Get books and categories for homepage
     * @return books and categories
     */
    @Operation(summary = "Get books and categories for homepage", description = "Get books and categories for homepage",
    responses = {
            @ApiResponse(responseCode = "200", description = "Get books and categories for homepage"),
            @ApiResponse(responseCode = "404", description = "Not found")
    })
    @GetMapping("/view-home")
    public ResponseEntity<?> getForHome(){
        List<Book> books = bookService.findBooks(6);
        List<Category> categories = categoryService.findAll();
        Map<String, Object> data = new HashMap<>();
        data.put("books", books);
        data.put("categories", categories);
        LOGGER.info("Get books and categories for homepage");
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
    /**
     * Get books for pagging
     * @return books
     */
    @GetMapping("/pagging")
    public ResponseEntity<?> getPagging(@RequestParam int page, @RequestParam(defaultValue = "3", required = false) int size){
        Pageable pageable = PageRequest.of(page,size);
        Page<Book> pageBook = bookService.getPagging(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("books", pageBook.getContent());
        data.put("totalBooks", pageBook.getTotalElements());
        data.put("totalPages",pageBook.getTotalPages());
        LOGGER.info("Get books for pagging page: "+ page);
        return new ResponseEntity<>(data, HttpStatus.OK);

    }
    /**
     * Add book
     * @return message
     */
    @PostMapping("/add-book")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addBook(@RequestHeader("Authorization") String token, @RequestParam String bookName, @RequestParam String author,
                                     @RequestParam String description, @RequestParam(required = false) MultipartFile image,
                                     @RequestParam double price, @RequestParam int categoryId, @RequestParam int quantity) throws IOException {
        String message = bookService.validateBook(bookName, author, description, price, categoryId, quantity);
        if(!"1".equals(message)){
            LOGGER.error("Add book failed with message: "+ message);
            return ResponseEntity.badRequest().body(new MessageResponse(message));
        }
        Book book = new Book();
        book.setBookName(bookName);
        book.setAuthor(author);
        book.setDescription(description);
        book.setPrice(price);
        if(image!= null){
            book.setImage(cloudinaryService.uploadFile(image));
        }
        book.setQuantity(quantity);
        Category category = categoryService.findById(categoryId);
        book.setCategory(category);
        bookService.saveOrUpdate(book);
        LOGGER.info("Book was added successfully!");
        return ResponseEntity.ok(new MessageResponse("Book was added successfully!"));
    }

    /**
     * Update book
     * @param id
     * return message
     */
    @PutMapping("/update-book/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public ResponseEntity<?> updateBookById(@PathVariable int id, @RequestParam(required = false) String bookName, @RequestParam(required = false) String author,
                                            @RequestParam(required = false) String description, @RequestParam(required = false) MultipartFile image,
                                            @RequestParam(required = false) Double price, @RequestParam(required = false) Integer categoryId, @RequestParam(required = false) Integer quantity ) throws IOException {
        Book book = bookService.findByBookId(id);
        if(bookName!= null) book.setBookName(bookName);
        if(description!= null) book.setDescription(description);
        if(image != null) book.setImage(cloudinaryService.uploadFile(image));
        if(author!= null) book.setAuthor(author);
        if(price != null) book.setPrice(price);
        if(categoryId != null) book.setCategory(categoryService.findById(categoryId));
        if(quantity != null) book.setQuantity(quantity);
        bookService.saveOrUpdate(book);
        LOGGER.info("Updated successfully!");
        return ResponseEntity.ok(new MessageResponse("Updated successfully!"));
    }
    /**
     * delete book
     * @param id
     * @return message
     */
    @DeleteMapping("/delete-book/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteBookById(@PathVariable int id){
        try{
            bookService.delete(id);
            return ResponseEntity.ok(new MessageResponse("Deleted successfully !"));
        }catch (Exception exception){
            LOGGER.error("Delete failed with id:"+ id );
            return ResponseEntity.badRequest().body(new MessageResponse("Delete failed"));
        }
    }
    /**
     * Get book detail
     * @param id
     * @return book detail
     */
    @GetMapping("/book/{id}")
    public ResponseEntity<Map<String, Object>> bookDetail(@PathVariable int id){
        Book book = bookService.findByBookId(id);
        if(book == null){
            LOGGER.error("Book not found with id: "+ id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Set<Comment> listCmt = commentService.findAllCommentByBookId(id);
        Map<String, Object> data = new HashMap<>();
        data.put("book", book);
        data.put("listCmt", listCmt);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
    @Operation(summary = "Get list of books with sort by multiple fields")
    @GetMapping("/list-with-sort-by-multiple-fields")
    public ResponseEntity<?> getAllBooksWithSortByMultipleFields(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(required = false) String... sorts) {
        LOGGER.info("Request get all of books with sort by multiple fields");
        PageResponse<?> pageResponse =  bookService.getAllBooksWithSortByMultipleField(pageNo, pageSize, sorts);
        return ResponseEntity.ok(pageResponse);
    }
    @Operation(summary = "Get list of books by custom query with searching, pagination and sorting")
    @GetMapping("/list-book-by-search-paging-and-sorting")
    public ResponseEntity<?> getAllBooksWithPagingAndSorting(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {
        LOGGER.info("Request get list of books by custom query with search, pagination and sorting");
        PageResponse<?> pageResponse =  bookService.getListBookBySearchPagingAndSorting(pageNo, pageSize,search, sort);
        return ResponseEntity.ok(pageResponse);
    }
    @Operation(summary = "Get list book with advance search by criteria")
    @GetMapping("/advance-search-by-criteria")
    public ResponseEntity<?> getListBookWithAdvanceSearchByCriteria(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(required = false) LocalDate cmtDate,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "", required = false) String... search) {
        LOGGER.info("search advance by criteria and paging and sorting");
        // search="bookName:a, author:d"
        return ResponseEntity.ok(bookService.getListBookWithAdvanceSearchByCriteria(pageNo,pageSize,cmtDate,sortBy,search));
    }
}
