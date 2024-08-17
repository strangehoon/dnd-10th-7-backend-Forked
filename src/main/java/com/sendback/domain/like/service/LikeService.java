package com.sendback.domain.like.service;

import com.sendback.domain.like.dto.response.ReactLikeResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String LIKE_KEY_PREFIX = "project:like:";
    private static final String PROJECT_SCORE_KEY = "project:score";

    @Transactional
    public ReactLikeResponseDto reactLike(Long userId, Long projectId) {
        String redisKey = LIKE_KEY_PREFIX + projectId;
        boolean isLiked = redisTemplate.opsForSet().isMember(redisKey, userId);

        String projectKey = "project_" + projectId;
        if (isLiked) {
            redisTemplate.opsForSet().remove(redisKey, userId);
            redisTemplate.opsForZSet().incrementScore(PROJECT_SCORE_KEY, projectKey, -1);
        } else {
            redisTemplate.opsForSet().add(redisKey, userId);
            redisTemplate.opsForZSet().incrementScore(PROJECT_SCORE_KEY, projectKey, 1);
        }

        // 변경 후의 상태를 반환합니다.
        boolean newLikeStatus = !isLiked;
        return new ReactLikeResponseDto(newLikeStatus);
    }
}