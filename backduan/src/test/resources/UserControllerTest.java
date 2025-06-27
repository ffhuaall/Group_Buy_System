package com.groupbuy.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.groupbuy.common.Result;
import com.groupbuy.dto.LoginRequest;
import com.groupbuy.dto.RegisterRequest;
import com.groupbuy.entity.User;
import com.groupbuy.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户控制器单元测试
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        // 创建测试用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("$2a$10$encrypted_password");
        testUser.setPhone("13800138001");
        testUser.setEmail("test@example.com");
        testUser.setRealName("测试用户");
        testUser.setGender("MALE");
        testUser.setStatus("ACTIVE");
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        // 创建登录请求
        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        // 创建注册请求
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("13900139001");
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setRealName("新用户");
        registerRequest.setGender("FEMALE");
    }

    @Test
    void testLogin_Success() throws Exception {
        // Given
        when(userService.login(any(LoginRequest.class)))
            .thenReturn(Result.success("登录成功", "mock_token"));

        // When & Then
        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("登录成功"))
                .andExpect(jsonPath("$.data").value("mock_token"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Given
        when(userService.login(any(LoginRequest.class)))
            .thenReturn(Result.error("用户名或密码错误"));

        // When & Then
        mockMvc.perform(post("/api/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户名或密码错误"));
    }

    @Test
    void testRegister_Success() throws Exception {
        // Given
        when(userService.register(any(RegisterRequest.class)))
            .thenReturn(Result.success("注册成功"));

        // When & Then
        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    void testRegister_UserExists() throws Exception {
        // Given
        when(userService.register(any(RegisterRequest.class)))
            .thenReturn(Result.error("用户名已存在"));

        // When & Then
        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户名已存在"));
    }

    @Test
    void testGetUserInfo_Success() throws Exception {
        // Given
        when(userService.getById(1L)).thenReturn(testUser);

        // When & Then
        mockMvc.perform(get("/api/user/info/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpected(jsonPath("$.data.phone").value("13800138001"));
    }

    @Test
    void testGetUserInfo_NotFound() throws Exception {
        // Given
        when(userService.getById(999L)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/user/info/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    void testUpdateUserInfo_Success() throws Exception {
        // Given
        User updateUser = new User();
        updateUser.setId(1L);
        updateUser.setRealName("更新后的姓名");
        updateUser.setPhone("13900139002");
        updateUser.setEmail("updated@example.com");

        when(userService.updateById(any(User.class))).thenReturn(true);

        // When & Then
        mockMvc.perform(put("/api/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("更新成功"));
    }

    @Test
    void testChangePassword_Success() throws Exception {
        // Given
        String changePasswordJson = "{\"oldPassword\":\"oldpass\",\"newPassword\":\"newpass\"}";
        when(userService.changePassword(eq(1L), eq("oldpass"), eq("newpass")))
            .thenReturn(Result.success("密码修改成功"));

        // When & Then
        mockMvc.perform(post("/api/user/change-password/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(changePasswordJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("密码修改成功"));
    }

    @Test
    void testChangePassword_WrongOldPassword() throws Exception {
        // Given
        String changePasswordJson = "{\"oldPassword\":\"wrongpass\",\"newPassword\":\"newpass\"}";
        when(userService.changePassword(eq(1L), eq("wrongpass"), eq("newpass")))
            .thenReturn(Result.error("原密码错误"));

        // When & Then
        mockMvc.perform(post("/api/user/change-password/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(changePasswordJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("原密码错误"));
    }

    @Test
    void testLogout_Success() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/user/logout")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("退出成功"));
    }

    @Test
    void testCheckUsername_Available() throws Exception {
        // Given
        when(userService.existsByUsername("availableuser")).thenReturn(false);

        // When & Then
        mockMvc.perform(get("/api/user/check-username")
                .param("username", "availableuser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("用户名可用"));
    }

    @Test
    void testCheckUsername_Taken() throws Exception {
        // Given
        when(userService.existsByUsername("takenuser")).thenReturn(true);

        // When & Then
        mockMvc.perform(get("/api/user/check-username")
                .param("username", "takenuser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value("用户名已被使用"));
    }
}