package com.blogrestapi.Dao;

import com.blogrestapi.Entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.blogrestapi.Entity.Comment;

import java.util.List;

public interface CommentDao extends MongoRepository<Comment,Integer> {
    Page<Comment> findCommentByPost(Post post, Pageable pageable);
}
