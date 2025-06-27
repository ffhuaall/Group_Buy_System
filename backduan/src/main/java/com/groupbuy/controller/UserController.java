package com.groupbuy.controller;

import com.groupbuy.common.Result;
import com.groupbuy.dto.LoginRequest;
import com.groupbuy.dto.RegisterRequest;
import com.groupbuy.entity.User;
import com.groupbuy.service.UserService;
import com.groupbuy.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8080")
public class UserController {
    
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterRequest request) {
        // 检查用户名是否已存在
        if (userService.existsByUsername(request.getUsername())) {
            return Result.error("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (userService.existsByPhone(request.getPhone())) {
            return Result.error("手机号已被注册");
        }
        
        // 创建用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setRealName(request.getRealName());
        user.setRole("USER");
        user.setStatus(1);
        
        userService.save(user);
        return Result.success("注册成功");
    }
    
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.findByUsername(request.getUsername());
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 验证密码
        System.out.println("输入的密码: " + request.getPassword());
        System.out.println("数据库中的哈希: " + user.getPassword());
        
        boolean passwordMatch = passwordEncoder.matches(request.getPassword(), user.getPassword());
        System.out.println("密码匹配结果: " + passwordMatch);
        
       
        if (user.getStatus() == 0) {
            return Result.error("账户已被禁用");
        }
        
        // 生成JWT token
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
        
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", user);
        
        return Result.success(result);
    }
    
    @GetMapping("/profile")
    public Result<User> getProfile(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        User user = userService.getById(userId);
        user.setPassword(null); // 不返回密码
        return Result.success(user);
    }
    
    @PutMapping("/profile")
    public Result<String> updateProfile(@RequestHeader("Authorization") String token,
                                       @RequestBody User userInfo) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 更新用户信息
        user.setRealName(userInfo.getRealName());
        user.setEmail(userInfo.getEmail());
        user.setGender(userInfo.getGender());
        user.setBirthday(userInfo.getBirthday());
        user.setAvatar(userInfo.getAvatar());
        
        userService.updateById(user);
        return Result.success("更新成功");
    }
    
    @PutMapping("/password")
    public Result<String> changePassword(@RequestHeader("Authorization") String token,
                                        @RequestBody Map<String, String> passwordData) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        
        String oldPassword = passwordData.get("oldPassword");
        String newPassword = passwordData.get("newPassword");
        
        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("原密码错误");
        }
        
        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateById(user);
        
        return Result.success("密码修改成功");
    }
    
    // 管理员功能：获取用户列表
    @GetMapping("/admin/list")
    public Result<Page<User>> getUserList(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestParam(required = false) String username,
                                         @RequestParam(required = false) String phone,
                                         @RequestParam(required = false) Integer status) {
        Page<User> users = userService.getUserList(current, size, username, phone, status);
        // 清除密码信息
        users.getRecords().forEach(user -> user.setPassword(null));
        return Result.success(users);
    }
    
    // 管理员功能：更新用户状态
    @PutMapping("/admin/status/{userId}")
    public Result<String> updateUserStatus(@PathVariable Long userId,
                                          @RequestBody Map<String, Integer> statusData) {
        Integer status = statusData.get("status");
        boolean success = userService.updateUserStatus(userId, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }
    
    // 管理员功能：获取用户统计信息
    @GetMapping("/admin/statistics")
    public Result<Map<String, Object>> getUserStatistics() {
        Map<String, Object> statistics = userService.getUserStatistics();
        return Result.success(statistics);
    }

}