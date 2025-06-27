package com.groupbuy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.entity.User;
import java.util.Map;

public interface UserService extends IService<User> {
    
    /**
     * 根据用户名查找用户
     */
    User findByUsername(String username);
    
    /**
     * 检查用户名是否存在
     */
    boolean existsByUsername(String username);
    
    /**
     * 检查手机号是否存在
     */
    boolean existsByPhone(String phone);
    
    /**
     * 根据手机号查找用户
     */
    User findByPhone(String phone);
    
    /**
     * 更新用户最后登录时间
     */
    void updateLastLoginTime(Long userId);
    
    /**
     * 获取用户列表（分页）
     */
    Page<User> getUserList(Integer current, Integer size, String username, String phone, Integer status);
    
    /**
     * 更新用户状态
     */
    boolean updateUserStatus(Long userId, Integer status);
    
    /**
     * 获取用户统计信息
     */
    Map<String, Object> getUserStatistics();
}