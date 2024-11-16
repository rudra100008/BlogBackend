package com.blogrestapi.Dao;

import com.blogrestapi.Entity.DisLike;
import com.blogrestapi.Entity.Post;
import com.blogrestapi.Entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DisLikeDao extends MongoRepository<DisLike,Integer> {
    DisLike findByUserAndPost(User user, Post post);
    Long countByPost(Post post);
    Boolean existsByUserAndPost(User user,Post post);
}
