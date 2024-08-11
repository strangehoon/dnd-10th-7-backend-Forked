package com.sendback.domain.auth.service;

import com.sendback.domain.auth.dto.Token;
import com.sendback.global.config.jwt.JwtProvider;
import com.sendback.global.config.redis.RedisService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static com.sendback.domain.auth.fixture.AuthFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    AuthService authService;
    @Mock
    RedisService redisService;
    @Mock
    JwtProvider jwtProvider;

    @Test
    @DisplayName("토큰 재발급시 access token, refresh token을 재발급한다.")
    void reissueToken() {
        // given
        String refresh_token = "valid_refresh_token";
        Long user_id = 123L;
        given(jwtProvider.parseRefreshToken(refresh_token)).willReturn(user_id);
        given(jwtProvider.issueToken(user_id)).willReturn(NEW_TOKEN);

        // when
        Token result = authService.reissueToken(refresh_token);

        // then
        assertNotNull(result);
        assertEquals(NEW_TOKEN.accessToken(), result.accessToken());
        assertEquals(NEW_TOKEN.refreshToken(), result.refreshToken());
    }

    @Test
    @DisplayName("로그아웃을 진행하면 redis에서 해당 유저의 refresh token을 삭제한다.")
    void logoutSocial() {
        // given
        Long user_id = 123L;
        doNothing().when(redisService).delete(user_id);

        // when
        authService.logoutSocial(user_id);

        // then
        verify(redisService, times(1)).delete(user_id);
    }
}