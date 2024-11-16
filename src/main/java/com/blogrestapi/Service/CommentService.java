package com.blogrestapi.Service;


import java.util.List;

import com.blogrestapi.DTO.PageResponse;
import com.blogrestapi.Entity.Comment;
import org.springframework.stereotype.Service;
import com.blogrestapi.DTO.CommentDTO;

@Service
public interface CommentService {
    List<CommentDTO> getAllComments();
    CommentDTO findCommentById(int commentId);
    CommentDTO createComments(CommentDTO commentDTO,int userId,int postId);
    CommentDTO updateComment(int commentId,CommentDTO commentDTO,int userId,int postId);
    void deleteComment(int commentId);
    PageResponse<CommentDTO> getCommentByPostId(int postId ,int pageNumber, int pageSize,String sortBy,String sortDir);
}
