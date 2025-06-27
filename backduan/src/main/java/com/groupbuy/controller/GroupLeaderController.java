package com.groupbuy.controller;

import com.groupbuy.common.Result;
import com.groupbuy.entity.GroupLeader;
import com.groupbuy.service.GroupLeaderService;
import com.groupbuy.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/groupleader")
public class GroupLeaderController {
    
    @Autowired
    private GroupLeaderService groupLeaderService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @PostMapping("/apply")
    public Result<GroupLeader> applyGroupLeader(@Valid @RequestBody GroupLeader groupLeader) {
        try {
            GroupLeader savedLeader = groupLeaderService.applyGroupLeader(groupLeader);
            return Result.success(savedLeader);
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public Result<List<GroupLeader>> getAllGroupLeaders(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status) {
        List<GroupLeader> leaders = groupLeaderService.getGroupLeaders(page, size, status);
        return Result.success(leaders);
    }
    
    @PutMapping("/approve/{id}")
    public Result<Void> approveGroupLeader(@PathVariable Long id) {
        groupLeaderService.approveGroupLeader(id);
        return Result.success();
    }
    
    @PutMapping("/reject/{id}")
    public Result<Void> rejectGroupLeader(@PathVariable Long id) {
        groupLeaderService.rejectGroupLeader(id);
        return Result.success();
    }
    
    @PutMapping("/status/{id}")
    public Result<Void> updateGroupLeaderStatus(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        String status = (String) request.get("status");
        groupLeaderService.updateGroupLeaderStatus(id, status);
        return Result.success();
    }
    
    @GetMapping("/nearby")
    public Result<List<GroupLeader>> getNearbyGroupLeaders(
            @RequestParam String city,
            @RequestParam String district) {
        List<GroupLeader> leaders = groupLeaderService.getNearbyGroupLeaders(city, district);
        return Result.success(leaders);
    }
    
    @GetMapping("/page")
    public Result<Page<GroupLeader>> getGroupLeadersPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String district) {
        Page<GroupLeader> leaders = groupLeaderService.getGroupLeadersPage(current, size, status, city, district);
        return Result.success(leaders);
    }
    
    @GetMapping("/profile/{id}")
    public Result<GroupLeader> getGroupLeaderProfile(@PathVariable Long id) {
        GroupLeader leader = groupLeaderService.getById(id);
        if (leader == null) {
            return Result.error("团长不存在");
        }
        return Result.success(leader);
    }
    
    @PutMapping("/profile/{id}")
    public Result<String> updateGroupLeaderProfile(@PathVariable Long id,
                                                  @RequestBody GroupLeader groupLeader,
                                                  @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        
        GroupLeader existingLeader = groupLeaderService.getById(id);
        if (existingLeader == null) {
            return Result.error("团长不存在");
        }
        
        // 检查权限：只有团长本人或管理员可以修改
        if (!existingLeader.getUserId().equals(userId)) {
            String role = jwtUtils.getRoleFromToken(actualToken);
            if (!"ADMIN".equals(role)) {
                return Result.error("无权限修改此团长信息");
            }
        }
        
        boolean success = groupLeaderService.updateGroupLeaderProfile(id, groupLeader);
        if (success) {
            return Result.success("更新成功");
        } else {
            return Result.error("更新失败");
        }
    }
    
    @GetMapping("/statistics/{id}")
    public Result<Map<String, Object>> getGroupLeaderStatistics(@PathVariable Long id) {
        Map<String, Object> statistics = groupLeaderService.getGroupLeaderStatistics(id);
        return Result.success(statistics);
    }
    
    @GetMapping("/admin/statistics")
    public Result<Map<String, Object>> getAllGroupLeaderStatistics() {
        Map<String, Object> statistics = groupLeaderService.getAllGroupLeaderStatistics();
        return Result.success(statistics);
    }
    
    @GetMapping("/my")
    public Result<GroupLeader> getMyGroupLeaderInfo(@RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        
        GroupLeader leader = groupLeaderService.getByUserId(userId);
        if (leader == null) {
            return Result.error("您还不是团长");
        }
        return Result.success(leader);
    }
    
    @PutMapping("/assign/{leaderId}")
    public Result<String> assignCommunityToLeader(@PathVariable Long leaderId, @RequestBody Map<String, Object> request) {
        try {
            Long communityId = null;
            Object communityIdObj = request.get("communityId");
            if (communityIdObj != null && !communityIdObj.toString().isEmpty()) {
                communityId = Long.valueOf(communityIdObj.toString());
            }
            
            groupLeaderService.assignCommunity(leaderId, communityId);
            return Result.success("分配成功");
        } catch (RuntimeException e) {
            return Result.error(e.getMessage());
        }
    }
}