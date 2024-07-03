package com.sparta.easyspring.commentlike.controller;

import com.sparta.easyspring.auth.config.SecurityConfig;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.auth.security.UserDetailsServiceImpl;
import com.sparta.easyspring.auth.util.JwtUtil;
import com.sparta.easyspring.commentlike.repository.CommentLikeRepository;
import com.sparta.easyspring.commentlike.service.CommentLikeService;
import com.sparta.easyspring.config.MockSpringSecurityFilter;
import com.sparta.easyspring.config.MockTestDataSetup;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.security.Principal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(controllers = {CommentLikeController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        })
class CommentLikeControllerTest{

    private MockMvc mvc;

    // 가짜 인증
    private static Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;


    @MockBean
    private CommentLikeService commentLikeService;

    @MockBean
    CommentLikeRepository commentLikeRepository;

    @MockBean
    UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    JwtUtil jwtUtil;


    // Mock 유저 설정
    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                // 만들어준 가짜 필터 적용
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }




    // 가짜 유저와 가짜 인증 객체 생성
    private static void mockUserSetup() {
        User TEST_USER = MockTestDataSetup.mockTestUserSetup(1L);

        UserDetailsImpl testUserDetails = new UserDetailsImpl(TEST_USER);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @BeforeAll
    static void beforeAll() {
        CommentLikeControllerTest.mockUserSetup();
    }

    @Test
    @DisplayName("성공 : 댓글 좋아요")
    void likeComment() throws Exception {
        // given
        long commentId = 1L;
        long userId = 1L;

        UserDetailsImpl userDetails = (UserDetailsImpl) ((UsernamePasswordAuthenticationToken) mockPrincipal).getPrincipal();
        User TEST_USER = userDetails.getUser();

        String expectedMsg = "댓글 좋아요 완료";
        String serviceResultMsg = "댓글 좋아요 완료";

        // Mock 데이터 설정
        Mockito.when(commentLikeService.likeComment(userId, commentId, TEST_USER))
                .thenReturn(ResponseEntity.ok(serviceResultMsg));

        // when - then
        mvc.perform(post("/like/comment/{userId}/{commentId}", userId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMsg));
    }


    @Test
    @DisplayName("성공 : 댓글 좋아요 해제")
    void unlikeComment() throws Exception {
        // given
        long commentId = 1L;
        long userId = 1L;

        UserDetailsImpl userDetails = (UserDetailsImpl) ((UsernamePasswordAuthenticationToken) mockPrincipal).getPrincipal();
        User TEST_USER = userDetails.getUser();

        String expectedMsg = "댓글 좋아요 해제 완료";

        // 좋아요 해제 메서드 호출에 대한 Mockito 설정
        Mockito.when(commentLikeService.unlikeComment(userId, commentId, TEST_USER))
                .thenReturn(ResponseEntity.ok(expectedMsg));

        // when - then
        mvc.perform(delete("/unlike/comment/{userId}/{commentId}", userId, commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMsg));
    }


}