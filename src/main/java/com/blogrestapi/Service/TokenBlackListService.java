package com.blogrestapi.Service;

import org.springframework.stereotype.Service;

@Service
public interface TokenBlackListService {
    void blackListToken(String token);
    boolean isTokenBlackListed(String token);
}
