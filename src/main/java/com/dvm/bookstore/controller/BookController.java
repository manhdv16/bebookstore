package com.dvm.bookstore.controller;

import com.dvm.bookstore.dto.request.BookCreationRequest;
import com.dvm.bookstore.dto.response.APIResponse;
import com.dvm.bookstore.dto.response.BookDetailResponse;
import com.dvm.bookstore.dto.response.CategoryResponse;
import com.dvm.bookstore.dto.response.PageResponse;
import com.dvm.bookstore.entity.Book;
import com.dvm.bookstore.entity.Comment;
import com.dvm.bookstore.service.BookService;
import com.dvm.bookstore.service.CategoryService;
import com.dvm.bookstore.service.CommentService;
import com.dvm.bookstore.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BookController {
    BookService bookService;
    CommentService commentService;
    CategoryService categoryService;
    RedisService redisService;
    static Logger LOGGER = LogManager.getLogger(BookController.class);

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
    public APIResponse<?> getDataForHome(){
        List<BookDetailResponse> books = new ArrayList<>();
        books =  redisService.getListBook("home-page");
        if(books == null || books.isEmpty()) {
            books = bookService.findBooks(0,6);
            redisService.setListBook("home-page", books);
            redisService.set("home-page", books, 60L);
        }
        List<CategoryResponse> categories = categoryService.findAll();
        Map<String, Object> data = new HashMap<>();
        data.put("books", books);
        data.put("categories", categories);

        return APIResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Get books and categories for homepage")
                .data(data)
                .build();
    }
    /**
     * Get books for pagging
     * @return books
     */
    @GetMapping("/pagging")
    public APIResponse<?> getPagging(@RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "3") int size) {
        if(page>0) page -=1;
        Pageable pageable = PageRequest.of(page,size);
        Page<BookDetailResponse> responsePage = bookService.getPagging(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("books", responsePage.getContent());
        data.put("totalBooks", responsePage.getTotalElements());
        data.put("totalPages",responsePage.getTotalPages());
        LOGGER.info("Get books for pagging page: "+ page);
        return APIResponse.<Map<String, Object>>builder()
                .code(200)
                .message("Get books for pagging")
                .data(data)
                .build();

    }
    /**
     * Add book
     * @return message
     */
    @PostMapping("/add-book")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public APIResponse<?> addBook(@ModelAttribute @Valid BookCreationRequest request) {
        bookService.addBook(request);
        return APIResponse.<String>builder()
                .code(200)
                .message("Add book successfully")
                .build();
    }

    /**
     * Update book
     * @param id
     * return message
     */
    @PutMapping("/update-book/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')")
    public APIResponse<?> updateBookById(@PathVariable int id,  BookCreationRequest request) throws IOException {
        bookService.updateBook(id, request);
        return APIResponse.<String>builder()
                .code(200)
                .message("Update book successfully")
                .build();
    }
    /**
     * delete book
     * @param id
     * @return message
     */
    @DeleteMapping("/delete-book/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public APIResponse<?> deleteBookById(@PathVariable int id){
        bookService.delete(id);
        return APIResponse.<String>builder()
                .code(200)
                .message("Delete book successfully")
                .build();
    }
    /**
     * Get book detail
     * @param id
     * @return book detail
     */
    @GetMapping("/book/{id}")
    public APIResponse<?> bookDetail(@PathVariable int id){
        Book book = bookService.findByBookId(id);
        Set<Comment> listCmt = commentService.findAllCommentByBookId(id);

        Map<String, Object> data = new HashMap<>();
        data.put("book", book);
        data.put("listCmt", listCmt);

        return APIResponse.<Map<String,Object>>builder()
                .code(200)
                .message("Found book by id successfully")
                .data(data)
                .build();
    }

    /**
     * Get list of books with sort by multiple fields
     * @param pageNo
     * @param pageSize
     * @param sorts
     * @return
     */
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

    /**
     * Get list of books by custom query with searching, pagination and sorting
     * @param pageNo
     * @param pageSize
     * @param search
     * @param sort
     * @return
     */
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

    /**
     * Get list book with advance search by criteria
     * @param pageNo
     * @param pageSize
     * @param cmtDate
     * @param sortBy
     * @param search
     * @return list book
     */
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
