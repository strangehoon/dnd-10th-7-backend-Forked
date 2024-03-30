package com.sendback.domain.user.controller;

import com.sendback.domain.auth.dto.response.TokensResponseDto;
import com.sendback.domain.user.dto.request.SignUpRequestDto;
import com.sendback.domain.user.dto.request.UpdateUserInfoRequestDto;
import com.sendback.domain.user.dto.response.*;
import com.sendback.domain.user.service.UserService;
import com.sendback.global.common.ApiResponse;
import com.sendback.global.common.CustomPage;
import com.sendback.global.common.UserId;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sendback.domain.auth.dto.Token;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users")
public class UserController {

    private final UserService userService;
    private final static String REFRESH_TOKEN = "refreshToken";

    @PostMapping("/signup")
    public ApiResponse<TokensResponseDto> signUpUser(@RequestBody @Valid SignUpRequestDto signUpRequestDto, HttpServletResponse response) {
        Token tokens = userService.signUpUser(signUpRequestDto);
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN, tokens.refreshToken())
                .maxAge(60*60*24*7)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        response.setHeader("set-cookie", cookie.toString());
        System.out.println(tokens.refreshToken());
        return ApiResponse.success(new TokensResponseDto(tokens.accessToken()));
    }

    @GetMapping("/check")
    public ApiResponse<CheckUserNicknameResponseDto> checkUserNickname(@RequestParam String nickname) {
        return ApiResponse.success(userService.checkUserNickname(nickname));
    }

    @GetMapping("/me")
    public ApiResponse<UserInfoResponseDto> getUserInfo(@UserId Long userId) {
        UserInfoResponseDto responseDto = userService.getUserInfo(userId);
        return ApiResponse.success(responseDto);
    }

    @PutMapping("/me")
    public ApiResponse<UpdateUserInfoResponseDto> updateUserInfo(@UserId Long userId, @RequestBody @Valid UpdateUserInfoRequestDto updateUserInfoRequestDto) {
        return ApiResponse.success(userService.updateUserInfo(userId, updateUserInfoRequestDto));
    }

    @GetMapping("/me/projects")
    public ApiResponse<CustomPage<RegisteredProjectResponseDto>> getRegisteredProjects(@UserId Long userId, @RequestParam int page,
                                                                                       @RequestParam int size, @RequestParam int sort) {
        return ApiResponse.success(userService.getRegisteredProjects(userId, page, size, sort));
    }

    @GetMapping("/me/scraps")
    public ApiResponse<CustomPage<ScrappedProjectResponseDto>> getScrappedProjects(@UserId Long userId, @RequestParam int page,
                                                                                     @RequestParam int size, @RequestParam int sort) {
        return ApiResponse.success(userService.getScrappedProjects(userId, page, size, sort));
    }

    @GetMapping("/me/feedbacks")
    public ApiResponse<CustomPage<SubmittedFeedbackResponseDto>> getSubmittedProjects(@UserId Long userId, @RequestParam int page,
                                                                                      @RequestParam int size, @RequestParam int sort) {
        return ApiResponse.success(userService.getSubmittedFeedback(userId, page, size, sort));
    }
}
