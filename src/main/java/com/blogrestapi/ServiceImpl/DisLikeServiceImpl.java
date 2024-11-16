package com.blogrestapi.ServiceImpl;

import com.blogrestapi.DTO.DisLikeDTO;
import com.blogrestapi.Dao.DisLikeDao;
import com.blogrestapi.Dao.LikeDao;
import com.blogrestapi.Dao.PostDao;
import com.blogrestapi.Dao.UserDao;
import com.blogrestapi.Entity.DisLike;
import com.blogrestapi.Entity.Like;
import com.blogrestapi.Entity.Post;
import com.blogrestapi.Entity.User;
import com.blogrestapi.Exception.AlreadyExistsException;
import com.blogrestapi.Exception.ResourceNotFoundException;
import com.blogrestapi.Service.DisLikeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DisLikeServiceImpl implements DisLikeService {
    @Autowired
    private DisLikeDao disLikeDao;
    @Autowired
    private LikeDao likeDao;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PostDao postDao;
    @Autowired
    private SequenceGeneratorService generatorService;
    @Override
    public synchronized DisLikeDTO postDelete(int userId, int postId) {
        Post post  = this.postDao.findById(postId)
                .orElseThrow(()-> new ResourceNotFoundException("Post not found by id: "+postId));
        User user =this.userDao.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found by id: "+userId));
        if(this.disLikeDao.existsByUserAndPost(user,post)){
           DisLike disLike = this.disLikeDao.findByUserAndPost(user,post);
           this.disLikeDao.delete(disLike);
           return null;
        }
        if(this.likeDao.existsByUserAndPost(user,post)){
            Like like = this.likeDao.findByUserAndPost(user,post);
            this.likeDao.delete(like);
        }

        DisLike newDislike =
                new DisLike((int) this.generatorService.generateSequence("dislike_sequence"),user,post);
        DisLike savedDislike = this.disLikeDao.save(newDislike);
        return this.modelMapper.map(savedDislike,DisLikeDTO.class);
    }

    @Override
    public Long countDislikes(int postId) {
        Post post  = this.postDao.findById(postId)
                .orElseThrow(()-> new ResourceNotFoundException("Post not found by id: "+postId));
        return this.disLikeDao.countByPost(post);
    }

    @Override
    public void removeDislike(int userId, int postId) {
        Post post  = this.postDao.findById(postId)
                .orElseThrow(()-> new ResourceNotFoundException("Post not found by id: "+postId));
        User user =this.userDao.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found by id: "+userId));
        DisLike disLike = this.disLikeDao.findByUserAndPost(user,post);
        if(disLike != null){
            this.disLikeDao.delete(disLike);
        }
    }
}
