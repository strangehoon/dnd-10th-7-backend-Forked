package com.sendback.global;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sendback.domain.auth.controller.AuthController;
import com.sendback.domain.auth.service.AuthService;
import com.sendback.domain.auth.service.GoogleService;
import com.sendback.domain.auth.service.KakaoService;

import com.sendback.domain.project.controller.ProjectController;
import com.sendback.domain.project.service.ProjectService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


@WebMvcTest({
        ProjectController.class,
        AuthController.class
})
@ActiveProfiles("test")
@AutoConfigureRestDocs
public abstract class ControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected ProjectService projectService;

    @MockBean
    protected KakaoService kakaoService;

    @MockBean
    protected GoogleService googleService;

    @MockBean
    protected AuthService authService;

}
