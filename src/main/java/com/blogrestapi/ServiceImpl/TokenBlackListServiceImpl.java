package com.blogrestapi.ServiceImpl;

import com.blogrestapi.Service.TokenBlackListService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TokenBlackListServiceImpl implements TokenBlackListService {
    Set<String> blackListedToken =new HashSet<>();
    @Override
    public void blackListToken(String token) {
        blackListedToken.add(token);
    }

    @Override
    public boolean isTokenBlackListed(String token) {
        return blackListedToken.contains(token);
    }
}
