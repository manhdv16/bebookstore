package com.dvm.bookstore.repository;

import com.dvm.bookstore.payload.response.PageResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
public class BookRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;
    private static final String LIKE_FORMAT = "%%%s%%";

    private static final Logger LOGGER = LogManager.getLogger(BookRepository.class);

    /**
     * search book with paging and sorting
     * @param pageNo
     * @param pageSize
     * @param search
     * @param sort
     * @return
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
}
