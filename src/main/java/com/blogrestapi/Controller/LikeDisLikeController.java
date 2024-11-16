package com.blogrestapi.Controller;

import com.blogrestapi.DTO.DisLikeDTO;
import com.blogrestapi.DTO.LikeDTO;
import com.blogrestapi.Service.DisLikeService;
import com.blogrestapi.Service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class LikeDisLikeController {
    @Autowired
    private LikeService likeService;
    @Autowired
    private DisLikeService disLikeService;
    @PostMapping("/likePost")
    public ResponseEntity<?> likePost(
            @RequestParam("userId") int userId,
            @RequestParam("postId") int postId
    ){
        LikeDTO like = this.likeService.postLike(userId,postId);
        return ResponseEntity.status(HttpStatus.OK).body(like);
    }
    @PostMapping("/dislikePost")
    public ResponseEntity<?> dislikePost(
            @RequestParam("userId") int userId,
            @RequestParam("postId") int postId
    ){
        DisLikeDTO disLike = this.disLikeService.postDelete(userId,postId);
        return ResponseEntity.status(HttpStatus.OK).body(disLike);
    }

}
