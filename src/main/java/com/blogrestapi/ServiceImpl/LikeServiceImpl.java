package com.blogrestapi.ServiceImpl;

import com.blogrestapi.DTO.LikeDTO;
import com.blogrestapi.Dao.DisLikeDao;
import com.blogrestapi.Dao.LikeDao;
import com.blogrestapi.Dao.PostDao;
import com.blogrestapi.Dao.UserDao;
import com.blogrestapi.Entity.DisLike;
import com.blogrestapi.Entity.Like;
import com.blogrestapi.Entity.Post;
import com.blogrestapi.Entity.User;
import com.blogrestapi.Exception.ResourceNotFoundException;
import com.blogrestapi.Service.LikeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private LikeDao likeDao;
    @Autowired
    private DisLikeDao disLikeDao;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserDao userDao;
    @Autowired
    private PostDao postDao;
    @Autowired
    private SequenceGeneratorService generatorService;
    @Override
    public synchronized LikeDTO postLike( int userId, int postId) {
        User user=this.userDao.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id: "+userId));
        Post post=this.postDao.findById(postId)
                .orElseThrow(()-> new ResourceNotFoundException("Post not found with id: "+postId));
        if(likeDao.existsByUserAndPost(user,post)){
           Like likePost = this.likeDao.findByUserAndPost(user, post);
           this.likeDao.delete(likePost);
           return null;
        }
        if(disLikeDao.existsByUserAndPost(user,post)){
            DisLike dislike =this.disLikeDao.findByUserAndPost(user,post);
            this.disLikeDao.delete(dislike);
        }
        Like newLike = new Like();
        newLike.setPost(post);
        newLike.setUser(user);
        newLike.setId((int)this.generatorService.generateSequence("like_sequence"));
        Like savedLike =this.likeDao.save(newLike);
        return  modelMapper.map(savedLike,LikeDTO.class);
    }

    @Override
    public Long countLikeForPost(int postId) {
        Post post =this.postDao.findById(postId)
                .orElseThrow(()->new ResourceNotFoundException("Post not found by id: "+postId));
        return this.likeDao.countByPost(post);
    }

    @Override
    public void removeLike(int userId, int postId) {
        User user=this.userDao.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found with id: "+userId));
        Post post=this.postDao.findById(postId)
                .orElseThrow(()-> new ResourceNotFoundException("Post not found with id: "+postId));
        Like like =this.likeDao.findByUserAndPost(user, post);
        if (like != null) {
            this.likeDao.delete(like);
        }
    }
}
