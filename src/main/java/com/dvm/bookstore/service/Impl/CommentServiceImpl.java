package com.dvm.bookstore.service.Impl;

import com.dvm.bookstore.repository.CommentReposirory;
import com.dvm.bookstore.service.CommentService;
import com.dvm.bookstore.entity.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * CommentServiceImpl class implements CommentService interface
 * @see CommentService
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentReposirory commentReposirory;
    @Override
    public List<Comment> findAll() {
        return commentReposirory.findAll();
    }
    @Override
    public void save(Comment comment) {
        commentReposirory.save(comment);
    }
    @Override
    public void delete(int id) {
        commentReposirory.deleteById(id);
    }
    @Override
    public Set<Comment> findAllCommentByUserId(int id) {
        return commentReposirory.findAllByUserId(id);
    }
    @Override
    public Set<Comment> findAllCommentByBookId(int id) {
        return commentReposirory.findAllByBookId(id);
    }
    @Override
    public Comment findById(int id) {
        return commentReposirory.findCommentByCommentId(id);
    }
}
