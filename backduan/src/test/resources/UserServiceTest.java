package com.groupbuy.service;

import com.groupbuy.entity.User;
import com.groupbuy.service.impl.UserServiceImpl;
import com.groupbuy.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 用户服务单元测试
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setPhone("13800138000");
        testUser.setEmail("test@example.com");
        testUser.setRealName("测试用户");
        testUser.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testFindByUsername_Success() {
        // Given
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(testUser);

        // When
        User result = userService.findByUsername("testuser");

        // Then
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class));
    }

    @Test
    void testFindByUsername_NotFound() {
        // Given
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        // When
        User result = userService.findByUsername("nonexistent");

        // Then
        assertNull(result);
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class));
    }

    @Test
    void testExistsByUsername_True() {
        // Given
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // When
        boolean exists = userService.existsByUsername("testuser");

        // Then
        assertTrue(exists);
        verify(userMapper, times(1)).selectCount(any(QueryWrapper.class));
    }

    @Test
    void testExistsByUsername_False() {
        // Given
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(0L);

        // When
        boolean exists = userService.existsByUsername("nonexistent");

        // Then
        assertFalse(exists);
        verify(userMapper, times(1)).selectCount(any(QueryWrapper.class));
    }

    @Test
    void testExistsByPhone_True() {
        // Given
        when(userMapper.selectCount(any(QueryWrapper.class))).thenReturn(1L);

        // When
        boolean exists = userService.existsByPhone("13800138000");

        // Then
        assertTrue(exists);
        verify(userMapper, times(1)).selectCount(any(QueryWrapper.class));
    }

    @Test
    void testFindByPhone_Success() {
        // Given
        when(userMapper.selectOne(any(QueryWrapper.class))).thenReturn(testUser);

        // When
        User result = userService.findByPhone("13800138000");

        // Then
        assertNotNull(result);
        assertEquals("13800138000", result.getPhone());
        verify(userMapper, times(1)).selectOne(any(QueryWrapper.class));
    }

    @Test
    void testUpdateLastLoginTime() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(testUser);
        when(userMapper.updateById(any(User.class))).thenReturn(1);

        // When
        userService.updateLastLoginTime(1L);

        // Then
        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, times(1)).updateById(any(User.class));
    }

    @Test
    void testUpdateLastLoginTime_UserNotFound() {
        // Given
        when(userMapper.selectById(1L)).thenReturn(null);

        // When
        userService.updateLastLoginTime(1L);

        // Then
        verify(userMapper, times(1)).selectById(1L);
        verify(userMapper, never()).updateById(any(User.class));
    }
}