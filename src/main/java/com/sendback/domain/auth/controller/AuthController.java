package com.sendback.domain.auth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sendback.domain.auth.dto.Token;
import com.sendback.domain.auth.dto.request.RefreshTokenRequestDto;
import com.sendback.domain.auth.dto.response.TokensResponseDto;
import com.sendback.domain.auth.service.AuthService;
import com.sendback.domain.auth.service.GoogleService;
import com.sendback.domain.auth.service.KakaoService;
import com.sendback.global.common.ApiResponse;
import com.sendback.global.common.UserId;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/auth")
public class AuthController {

    private final KakaoService kakaoService;
    private final GoogleService googleService;
    private final AuthService authService;
    @Value("${jwt.refresh-token-expire-time}")
    private long REFRESH_TOKEN_EXPIRE_TIME;
    private final static String REFRESH_TOKEN = "refreshToken";

    @GetMapping("/kakao/callback")
    public ApiResponse<TokensResponseDto> loginKakao(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        Token tokens = kakaoService.loginKakao(code);
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, tokens.refreshToken())
                .maxAge(60*60*24*7)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        response.setHeader("set-cookie", cookie.toString());
        return ApiResponse.success(new TokensResponseDto(tokens.accessToken()));
    }

    @GetMapping("/google/callback")
    public ApiResponse<TokensResponseDto> loginGoogle(@RequestParam String code, HttpServletResponse response) throws JsonProcessingException {
        Token tokens = googleService.loginGoogle(code);
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, tokens.refreshToken())
                .maxAge(60*60*24*7)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        response.setHeader("set-cookie", cookie.toString());
        return ApiResponse.success(new TokensResponseDto(tokens.accessToken()));
    }

    @PostMapping("/reissue")
    public ApiResponse<TokensResponseDto> reissueToken(@RequestBody RefreshTokenRequestDto refreshTokenDto, HttpServletResponse response){
        Token tokens = authService.reissueToken(refreshTokenDto.refreshToken());
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, tokens.refreshToken())
                .maxAge(60*60*24*7)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        response.setHeader("set-cookie", cookie.toString());
        return ApiResponse.success(new TokensResponseDto(tokens.accessToken()));
    }

    @PostMapping("/logout")
    public ApiResponse<String> logoutSocial(@UserId Long userId){
        authService.logoutSocial(userId);
        return ApiResponse.success(null);
    }

}
