package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.entity.Book;
import com.dvm.bookstore.payload.response.BookDetailResponse;
import com.dvm.bookstore.payload.response.PageResponse;
import com.dvm.bookstore.repository.BookRepository;
import com.dvm.bookstore.repository.custom.BookRepositoryCustom;
import com.dvm.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * BookServiceImpl class implements BookService interface
 * This class is used to implement all the methods in BookService interface
 * This class is used to interact with the database
 */
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookRepositoryCustom bookRepositoryCustom;
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

    /**
     * get all book by pageNo and pageSize with sort by multiple field
     * @param pageNo
     * @param pageSize
     * @param sorts
     * @return PageResponse
     */
    @Override
    public PageResponse<?> getAllBooksWithSortByMultipleField(int pageNo, int pageSize, String... sorts) {
        if(pageNo>0) pageNo--;
        List<Sort.Order> orders = new ArrayList<>();
        if(sorts!=null) {
            for(String sortBy : sorts) {
                LOGGER.info("sort by: {}", sortBy);
                // sortBy: "name:asc"
                String[] sort = sortBy.split(":");
                if(sort.length>1) {
                    if(sort[1].equalsIgnoreCase("asc")){
                        orders.add(new Sort.Order(Sort.Direction.ASC, sort[0]));
                    } else if (sort[1].equalsIgnoreCase("desc")){
                        orders.add(new Sort.Order(Sort.Direction.DESC, sort[0]));
                    }
                }
            }
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(orders));
        Page<Book> books = bookRepository.findAll(pageable);
        return convertToPageResponse(books, pageable);
    }

    /**
     * get all book f
     * @param pageNo
     * @param pageSize
     * @param search
     * @param sort
     * @return
     */
    @Override
    public PageResponse<?> getListBookBySearchPagingAndSorting(int pageNo, int pageSize, String search, String sort) {
        return bookRepositoryCustom.searchBook(pageNo,pageSize,search,sort);
    }

    /**
     * advance search to get list book with paging and sorting
     * @param pageNo
     * @param pageSize
     * @param sortBy
     * @param search
     * @return pageresponse
     */
    @Override
    public PageResponse<?> getListBookWithAdvanceSearchByCriteria(int pageNo, int pageSize, LocalDate cmtDate, String sortBy, String... search) {
        return bookRepositoryCustom.searchAdvanceByCriteria(pageNo,pageSize,cmtDate,sortBy,search);
    }

    /**
     * convert Page<Book> to PageResponse
     * @param books
     * @param pageable
     * @return PageResponse
     */
    private PageResponse<?> convertToPageResponse(Page<Book> books, Pageable pageable) {
        List<BookDetailResponse> response = books.stream().map(book -> BookDetailResponse.builder()
                .id(book.getBookId())
                .bookName(book.getBookName())
                .description(book.getDescription())
                .author(book.getAuthor())
                .image(book.getImage())
                .price(book.getPrice())
                .quantity(book.getQuantity())
                .sold(book.getSold())
                .categoryName(book.getCategory().getCategoryName())
                .build()).toList();

        return PageResponse.builder()
                .page(pageable.getPageNumber())
                .size(pageable.getPageSize())
                .totalPages(books.getTotalPages())
                .items(response)
                .build();
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

