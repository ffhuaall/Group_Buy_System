package com.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.entity.User;
import com.groupbuy.mapper.UserMapper;
import com.groupbuy.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Override
    public User findByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.getOne(queryWrapper);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return this.count(queryWrapper) > 0;
    }
    
    @Override
    public boolean existsByPhone(String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        return this.count(queryWrapper) > 0;
    }
    
    @Override
    public User findByPhone(String phone) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        return this.getOne(queryWrapper);
    }
    
    @Override
    public void updateLastLoginTime(Long userId) {
        User user = this.getById(userId);
        if (user != null) {
            user.setUpdatedAt(LocalDateTime.now());
            this.updateById(user);
        }
    }
    
    @Override
    public Page<User> getUserList(Integer current, Integer size, String username, String phone, Integer status) {
        Page<User> page = new Page<>(current, size);
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        if (phone != null && !phone.isEmpty()) {
            queryWrapper.like("phone", phone);
        }
        if (status != null) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("created_at");
        return this.page(page, queryWrapper);
    }
    
    @Override
    public boolean updateUserStatus(Long userId, Integer status) {
        User user = this.getById(userId);
        if (user != null) {
            user.setStatus(status);
            user.setUpdatedAt(LocalDateTime.now());
            return this.updateById(user);
        }
        return false;
    }
    
    @Override
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总用户数
        Long totalUsers = this.count();
        statistics.put("totalUsers", totalUsers);
        
        // 活跃用户数
        QueryWrapper<User> activeWrapper = new QueryWrapper<>();
        activeWrapper.eq("status", 1);
        Long activeUsers = this.count(activeWrapper);
        statistics.put("activeUsers", activeUsers);
        
        // 禁用用户数
        QueryWrapper<User> disabledWrapper = new QueryWrapper<>();
        disabledWrapper.eq("status", 0);
        Long disabledUsers = this.count(disabledWrapper);
        statistics.put("disabledUsers", disabledUsers);
        
        // 今日新增用户数
        QueryWrapper<User> todayWrapper = new QueryWrapper<>();
        todayWrapper.ge("created_at", LocalDateTime.now().toLocalDate());
        Long todayNewUsers = this.count(todayWrapper);
        statistics.put("todayNewUsers", todayNewUsers);
        
        // 本月新增用户数
        QueryWrapper<User> monthWrapper = new QueryWrapper<>();
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        monthWrapper.ge("created_at", monthStart);
        Long monthNewUsers = this.count(monthWrapper);
        statistics.put("monthNewUsers", monthNewUsers);
        
        // 各角色用户数
        QueryWrapper<User> userRoleWrapper = new QueryWrapper<>();
        userRoleWrapper.eq("role", "USER");
        Long normalUsers = this.count(userRoleWrapper);
        statistics.put("normalUsers", normalUsers);
        
        QueryWrapper<User> adminRoleWrapper = new QueryWrapper<>();
        adminRoleWrapper.eq("role", "ADMIN");
        Long adminUsers = this.count(adminRoleWrapper);
        statistics.put("adminUsers", adminUsers);
        
        return statistics;
    }
}