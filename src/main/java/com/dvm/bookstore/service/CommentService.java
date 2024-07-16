package com.dvm.bookstore.service;

import com.dvm.bookstore.entity.Comment;

import java.util.List;
import java.util.Set;

public interface CommentService {
    List<Comment> findAll();
    void save(Comment comment);
    Boolean delete(int id);
    Set<Comment> findAllCommentByUserId(int id);
    Set<Comment> findAllCommentByBookId(int id);
    Comment findById(int id);
}
