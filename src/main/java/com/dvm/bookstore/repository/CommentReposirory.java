package com.dvm.bookstore.repository;

import com.dvm.bookstore.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Set;
@Repository
public interface CommentReposirory extends JpaRepository<Comment,Integer> {
    @Query("select c from Comment c where c.user.userId = ?1")
    Set<Comment> findAllByUserId(int userId);
    @Query("select c from Comment c where c.book.bookId=?1")
    Set<Comment> findAllByBookId(int bookId);
    Comment findCommentByCommentId(int id);
}
