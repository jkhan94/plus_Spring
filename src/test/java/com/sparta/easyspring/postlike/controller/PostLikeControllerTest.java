package com.sparta.easyspring.postlike.controller;

import com.sparta.easyspring.MockSpringSecurityFilter;
import com.sparta.easyspring.auth.config.SecurityConfig;
import com.sparta.easyspring.auth.entity.User;
import com.sparta.easyspring.auth.security.UserDetailsImpl;
import com.sparta.easyspring.auth.security.UserDetailsServiceImpl;
import com.sparta.easyspring.auth.util.JwtUtil;
import com.sparta.easyspring.postlike.config.MockTestDataSetup;
import com.sparta.easyspring.postlike.repository.PostLikeRepository;
import com.sparta.easyspring.postlike.service.PostLikeService;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@WebMvcTest(controllers = {PostLikeController.class},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        })
class PostLikeControllerTest {
    private MockMvc mvc;

    private static Principal mockPrincipal;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    PostLikeService postLikeService;

    @MockBean
    PostLikeRepository postLikeRepository;

    @MockBean
    UserDetailsServiceImpl userDetailsServiceImpl;

    @MockBean
    JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity(new MockSpringSecurityFilter()))
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    private static void mockUserSetup() {
        User TEST_USER = MockTestDataSetup.mockTestUserSetup();

        UserDetailsImpl testUserDetails = new UserDetailsImpl(TEST_USER);
        mockPrincipal = new UsernamePasswordAuthenticationToken(testUserDetails, "", testUserDetails.getAuthorities());
    }

    @BeforeAll
    static void beforeAll() {
        PostLikeControllerTest.mockUserSetup();
    }

    @Test
    @Order(1)
    @DisplayName("성공 : 게시글 좋아요")
    void likePost() throws Exception {
        // given
        long postId = 1L;
        long userId = 1L;

        String expectedMsg = "게시글 좋아요 완료";
        String serviceResultMsg = "게시글 좋아요 완료";

        Mockito.when(postLikeService.likePost(userId, postId)).thenReturn(serviceResultMsg);

        // when - then
        mvc.perform(post("/like/post/{userId}/{postId}", userId, postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMsg))
                .andDo(print());
    }


    @Test
    @Order(2)
    @DisplayName("실패 : 좋아요 - 인증되지 않은 사용자")
    void likePostException() throws Exception {
        // given
        long postId = 1L;
        long userId = 2L;

        String expectedMsg = "인증되지 않은 사용자입니다. 로그인해주세요.";

        // when - then
        mvc.perform(post("/like/post/{userId}/{postId}", userId, postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(expectedMsg))
                .andDo(print());
    }


    @Test
    @Order(3)
    @DisplayName("성공 : 게시글 좋아요 해제")
    void unlikePost() throws Exception {
        // given
        long postId = 1L;
        long userId = 1L;

        String expectedMsg = "게시글 좋아요 해제 완료";
        String serviceResultMsg = "게시글 좋아요 해제 완료";

        Mockito.when(postLikeService.unlikePost(userId, postId)).thenReturn(serviceResultMsg);

        // when - then
        mvc.perform(delete("/unlike/post/{userId}/{postId}", userId, postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMsg))
                .andDo(print());
    }


    @Test
    @Order(4)
    @DisplayName("실패 : 좋아요 해제 - 인증되지 않은 사용자")
    void unlikePostException() throws Exception {
        // given
        long postId = 1L;
        long userId = 2L;

        String expectedMsg = "인증되지 않은 사용자입니다. 로그인해주세요.";

        // when - then
        mvc.perform(delete("/unlike/post/{userId}/{postId}", userId, postId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .principal(mockPrincipal)
                )
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(expectedMsg))
                .andDo(print());
    }
}