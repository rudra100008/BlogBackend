package com.blogrestapi.Service;

import com.blogrestapi.DTO.LikeDTO;
import org.springframework.stereotype.Service;

@Service
public interface LikeService {
    LikeDTO  postLike(int userId,int postId);
    Long countLikeForPost(int postId);
    void removeLike(int userId,int postId);

}
