package com.blogrestapi.Service;

import com.blogrestapi.DTO.DisLikeDTO;
import org.springframework.stereotype.Service;

@Service
public interface DisLikeService {
    DisLikeDTO postDelete(int userId, int postId);
    Long countDislikes(int postId);
    void removeDislike(int userId, int postId);
}
