package com.dvm.bookstore.repository.custom;

import com.dvm.bookstore.entity.Book;
import com.dvm.bookstore.entity.Comment;
import com.dvm.bookstore.dto.response.PageResponse;
import com.dvm.bookstore.repository.BookRepository;
import com.dvm.bookstore.repository.custom.criteria.BookSearchQueryCriteriaConsumer;
import com.dvm.bookstore.repository.custom.criteria.SearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private static final String LIKE_FORMAT = "%%%s%%"; //  %% %s %% , %%-> %

    private static final Logger LOGGER = LogManager.getLogger(BookRepository.class);

    /**
     * search book with paging and sorting
     * @param pageNo
     * @param pageSize
     * @param search
     * @param sort
     * @return PageResponse
     */
    public PageResponse<?> searchBook(int pageNo, int pageSize, String search, String sort) {
        LOGGER.info("Excute search book with keyword={}", search);
        StringBuilder sqlQuery = new StringBuilder(
                "select new com.dvm.bookstore.payload.response.BookDetailResponse(b.bookId,b.bookName,b.description,b.author,b.image,b.price,b.quantity,b.sold,b.category.categoryName) from Book b");
        if (StringUtils.hasLength(search)) {
            sqlQuery.append(" where lower(b.bookName) like lower(?1)");
            sqlQuery.append(" or lower(b.author) like lower(?2)");
        }
        if(StringUtils.hasLength(sort)) {
            // fileName:asc|desc
            String[] arrSort = sort.split(":");
            if(arrSort.length>0) {
                sqlQuery.append(String.format(" order by b.%s %s", arrSort[0], arrSort[1]));
            }
        }
        Query selectQuery = entityManager.createQuery(sqlQuery.toString());
        selectQuery.setFirstResult(pageNo);
        selectQuery.setMaxResults(pageSize);
        // search
        if (StringUtils.hasLength(search)) {
            selectQuery.setParameter(1, String.format(LIKE_FORMAT, search));
            selectQuery.setParameter(2, String.format(LIKE_FORMAT, search));
        }
        List<?> books = selectQuery.getResultList();
        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .items(books).build();
    }

    /**
     * search advance with criteria
     * @param pageNo
     * @param pageSize
     * @param cmtDate
     * @param sortBy
     * @param search
     * @return
     */
    public PageResponse<?> searchAdvanceByCriteria(int pageNo, int pageSize, LocalDate cmtDate, String sortBy, String... search) {
        LOGGER.info("search book with search={}", search, sortBy);
        List<SearchCriteria> criteriaList = new ArrayList<>();
        //search=bookName:sadf
        if(search.length>0) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|>|<)(.*)");
            Matcher matcher;
            for(String s : search) {
                matcher = pattern.matcher(s);
                if(matcher.find()) {
                    criteriaList.add(new SearchCriteria(matcher.group(1),matcher.group(2),matcher.group(3)));
                }
            }
        }
        List<Book> books = getBooks(pageNo,pageSize,cmtDate,criteriaList,sortBy);
        return PageResponse.builder()
                .page(pageNo)
                .size(pageSize)
                .items(books).build();
    }

    private List<Book> getBooks(int pageNo, int pageSize,LocalDate cmtDate, List<SearchCriteria> criteriaList, String sortBy) {

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = criteriaBuilder.createQuery(Book.class);
        Root<Book> bookRoot = query.from(Book.class);

        Predicate bookPredicate = criteriaBuilder.conjunction();
        BookSearchQueryCriteriaConsumer searchConsumer = new BookSearchQueryCriteriaConsumer(bookPredicate,criteriaBuilder,bookRoot);
        // find book by comment date
        if(cmtDate!=null) {
            Join<Comment, Book> bookCommentJoin = bookRoot.join("comments");
            Predicate commentPredicate = criteriaBuilder.equal(bookCommentJoin.get("cmtDate"),cmtDate);
            query.where(bookPredicate,commentPredicate);
        } else {
            criteriaList.forEach(searchConsumer);
            bookPredicate = searchConsumer.getPredicate();
            query.where(bookPredicate);
        }
        // gia su tim kiem tren tat ca cac truong cua comment;
        // comments[] = {"cmtDate:asc|desc", "rating>4"}
        // sort by
        if(StringUtils.hasLength(sortBy)) {
            Pattern pattern = Pattern.compile("(\\w+?)(:)(asc|desc)");
            Matcher matcher = pattern.matcher(sortBy);
            if(matcher.find()) {
                String fieldName = matcher.group(1);
                String direction = matcher.group(3);
                if(direction.equalsIgnoreCase("asc")) {
                    query.orderBy(criteriaBuilder.asc(bookRoot.get(fieldName)));
                }else {
                    query.orderBy(criteriaBuilder.desc(bookRoot.get(fieldName)));
                }
            }
        }
        return entityManager.createQuery(query)
                .setFirstResult(pageNo)
                .setMaxResults(pageSize)
                .getResultList();
    }
}
