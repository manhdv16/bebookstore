package dvm.springbootweb.controller;

import dvm.springbootweb.cloudinary.CloudinaryService;
import dvm.springbootweb.entity.Book;
import dvm.springbootweb.entity.Category;
import dvm.springbootweb.entity.Comment;
import dvm.springbootweb.payload.response.MessageResponse;
import dvm.springbootweb.service.BookService;
import dvm.springbootweb.service.CategoryService;
import dvm.springbootweb.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


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
     * @return
     */
    @GetMapping("/view-home")
    public ResponseEntity<?> getForHome(){
        List<Book> books = bookService.findBooks(6);
        List<Category> categories = categoryService.findAll();
        Map<String, Object> data = new HashMap<>();
        data.put("books", books);
        data.put("categories", categories);
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
    /**
     * Get books for pagging
     * @return
     */
    @GetMapping("/pagging")
    public ResponseEntity<?> getPagging(@RequestParam(required = true) int page){
        int sizeDefault = 3;
        Pageable pageable = PageRequest.of(page,sizeDefault);
        Page<Book> pageBook = bookService.getPagging(pageable);
        Map<String, Object> data = new HashMap<>();
        data.put("books", pageBook.getContent());
        data.put("totalBooks", pageBook.getTotalElements());
        data.put("totalPages",pageBook.getTotalPages());
        return new ResponseEntity<>(data, HttpStatus.OK);
    }
    /**
     * Add book
     * @return
     */
    @PostMapping("/add-book")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> addBook(@RequestHeader("Authorization") String token, @RequestParam String bookName, @RequestParam String author,
                                     @RequestParam String description, @RequestParam(required = false) MultipartFile image,
                                     @RequestParam double price, @RequestParam int categoryId, @RequestParam int quantity) throws IOException {
        String message = bookService.validateBook(bookName, author, description, price, categoryId, quantity);
        if(!"1".equals(message)){
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
        return ResponseEntity.ok(new MessageResponse("Book was added successfully!"));
    }

    /**
     * Update book
     * @param id
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
        return ResponseEntity.ok(new MessageResponse("Updated successfully!"));
    }

    /**
     * delete book
     * @param id
     * @return
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
     * @return
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
}
