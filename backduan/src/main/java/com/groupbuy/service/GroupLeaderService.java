package com.groupbuy.service;

import com.groupbuy.entity.GroupLeader;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;
import java.util.Map;

public interface GroupLeaderService extends IService<GroupLeader> {
    GroupLeader applyGroupLeader(GroupLeader groupLeader);
    List<GroupLeader> getGroupLeaders(int page, int size, String status);
    void approveGroupLeader(Long id);
    void rejectGroupLeader(Long id);
    List<GroupLeader> getNearbyGroupLeaders(String city, String district);
    
    /**
     * 分页获取团长列表
     */
    Page<GroupLeader> getGroupLeadersPage(Integer current, Integer size, String status, String city, String district);
    
    /**
     * 更新团长信息
     */
    boolean updateGroupLeaderProfile(Long id, GroupLeader groupLeader);
    
    /**
     * 获取团长统计信息
     */
    Map<String, Object> getGroupLeaderStatistics(Long id);
    
    /**
     * 获取所有团长统计信息
     */
    Map<String, Object> getAllGroupLeaderStatistics();
    
    /**
     * 根据用户ID获取团长信息
     */
    GroupLeader getByUserId(Long userId);
    
    /**
     * 更新团长状态
     */
    void updateGroupLeaderStatus(Long id, String status);
    
    /**
     * 为团长分配社区
     */
    void assignCommunity(Long leaderId, Long communityId);
}