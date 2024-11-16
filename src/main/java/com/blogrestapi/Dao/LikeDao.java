package com.blogrestapi.Dao;

import com.blogrestapi.Entity.Like;
import com.blogrestapi.Entity.Post;
import com.blogrestapi.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeDao extends MongoRepository<Like,Integer> {
    Like findByUserAndPost(User user, Post post);
    Long countByPost(Post post);
    boolean existsByUserAndPost(User user, Post post);
}
