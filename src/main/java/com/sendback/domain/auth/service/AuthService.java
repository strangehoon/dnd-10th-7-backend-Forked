package com.sendback.domain.auth.service;

import com.sendback.domain.auth.dto.Token;
import com.sendback.global.config.redis.RedisService;
import com.sendback.global.config.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    @Transactional
    public Token reissueToken(String refreshToken) {
        Long userId = jwtProvider.parseRefreshToken(refreshToken);
        Token tokens = jwtProvider.issueToken(userId);
        return tokens;
    }

    public void logoutSocial(Long userId){
        redisService.delete(userId);
    }
}
