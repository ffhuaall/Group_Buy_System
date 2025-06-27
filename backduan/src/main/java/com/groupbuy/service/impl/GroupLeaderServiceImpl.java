package com.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.groupbuy.entity.GroupLeader;
import com.groupbuy.mapper.GroupLeaderMapper;
import com.groupbuy.service.GroupLeaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Service
public class GroupLeaderServiceImpl extends ServiceImpl<GroupLeaderMapper, GroupLeader> implements GroupLeaderService {
    
    private static final Logger log = LoggerFactory.getLogger(GroupLeaderServiceImpl.class);
    
    private static final Logger logger = LoggerFactory.getLogger(GroupLeaderServiceImpl.class);
    
    @Autowired
    private GroupLeaderMapper groupLeaderMapper;
    
    @Override
    @Transactional
    public GroupLeader applyGroupLeader(GroupLeader groupLeader) {
        try {
            logger.info("开始处理团长申请，用户ID: {}", groupLeader.getUserId());
            logger.info("申请信息: 姓名={}, 电话={}, 城市={}, 区域={}, 地址={}", 
                groupLeader.getRealName(), groupLeader.getPhone(), 
                groupLeader.getCity(), groupLeader.getDistrict(), groupLeader.getAddress());
            
            // 检查用户是否已经申请过团长
            QueryWrapper<GroupLeader> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", groupLeader.getUserId());
            GroupLeader existingApplication = groupLeaderMapper.selectOne(queryWrapper);
            
            if (existingApplication != null) {
                logger.warn("用户ID: {} 已经申请过团长，状态: {}", groupLeader.getUserId(), existingApplication.getStatus());
                throw new RuntimeException("您已经申请过团长，请勿重复申请。当前申请状态: " + getStatusText(existingApplication.getStatus()));
            }
            
            // 设置默认值
            groupLeader.setStatus("PENDING");
            groupLeader.setCreatedAt(LocalDateTime.now());
            groupLeader.setUpdatedAt(LocalDateTime.now());
            
            // 根据申请信息设置社区相关字段
            if (groupLeader.getCity() != null && groupLeader.getDistrict() != null) {
                groupLeader.setCommunityName(groupLeader.getCity() + groupLeader.getDistrict());
            }
            if (groupLeader.getAddress() != null) {
                groupLeader.setCommunityAddress(groupLeader.getAddress());
            }
            if (groupLeader.getReason() != null) {
                groupLeader.setDescription(groupLeader.getReason());
            }
            
            logger.info("准备插入数据库，社区名称: {}, 社区地址: {}", 
                groupLeader.getCommunityName(), groupLeader.getCommunityAddress());
            
            groupLeaderMapper.insert(groupLeader);
            
            logger.info("团长申请处理成功，生成ID: {}", groupLeader.getId());
            return groupLeader;
        } catch (Exception e) {
            logger.error("团长申请处理失败", e);
            throw new RuntimeException("申请团长失败: " + e.getMessage(), e);
        }
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "PENDING":
                return "待审核";
            case "APPROVED":
                return "已通过";
            case "REJECTED":
                return "已拒绝";
            case "SUSPENDED":
                return "已暂停";
            default:
                return status;
        }
    }
    
    @Override
    public List<GroupLeader> getGroupLeaders(int page, int size, String status) {
        Page<GroupLeader> pageObj = new Page<>(page, size);
        QueryWrapper<GroupLeader> wrapper = new QueryWrapper<>();
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("created_at");
        return groupLeaderMapper.selectPage(pageObj, wrapper).getRecords();
    }
    
    @Override
    @Transactional
    public void approveGroupLeader(Long id) {
        GroupLeader groupLeader = groupLeaderMapper.selectById(id);
        if (groupLeader != null) {
            groupLeader.setStatus("APPROVED");
            groupLeader.setUpdatedAt(LocalDateTime.now());
            groupLeaderMapper.updateById(groupLeader);
        }
    }
    
    @Override
    @Transactional
    public void rejectGroupLeader(Long id) {
        GroupLeader groupLeader = groupLeaderMapper.selectById(id);
        if (groupLeader != null) {
            groupLeader.setStatus("REJECTED");
            groupLeader.setUpdatedAt(LocalDateTime.now());
            groupLeaderMapper.updateById(groupLeader);
        }
    }
    
    @Override
    public List<GroupLeader> getNearbyGroupLeaders(String city, String district) {
        QueryWrapper<GroupLeader> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "APPROVED")
               .like("community_address", city)
               .like("community_address", district);
        return groupLeaderMapper.selectList(wrapper);
    }
    
    @Override
    public Page<GroupLeader> getGroupLeadersPage(Integer current, Integer size, String status, String city, String district) {
        Page<GroupLeader> page = new Page<>(current, size);
        QueryWrapper<GroupLeader> queryWrapper = new QueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        if (city != null && !city.isEmpty()) {
            queryWrapper.like("community_address", city);
        }
        if (district != null && !district.isEmpty()) {
            queryWrapper.like("community_address", district);
        }
        
        queryWrapper.orderByDesc("created_at");
        return this.page(page, queryWrapper);
    }
    
    @Override
    @Transactional
    public boolean updateGroupLeaderProfile(Long id, GroupLeader groupLeader) {
        GroupLeader existingLeader = this.getById(id);
        if (existingLeader != null) {
            existingLeader.setRealName(groupLeader.getRealName());
            existingLeader.setPhone(groupLeader.getPhone());
            existingLeader.setCommunityName(groupLeader.getCommunityName());
            existingLeader.setCommunityAddress(groupLeader.getCommunityAddress());
            existingLeader.setDescription(groupLeader.getDescription());
            existingLeader.setUpdatedAt(LocalDateTime.now());
            return this.updateById(existingLeader);
        }
        return false;
    }
    
    @Override
    public Map<String, Object> getGroupLeaderStatistics(Long id) {
        Map<String, Object> statistics = new HashMap<>();
        
        GroupLeader leader = this.getById(id);
        if (leader == null) {
            return statistics;
        }
        
        statistics.put("groupLeader", leader);
        
        // 这里可以添加更多统计信息，比如订单数量、销售额等
        // 由于需要关联订单表，暂时返回基本信息
        statistics.put("joinDate", leader.getCreatedAt());
        statistics.put("status", leader.getStatus());
        
        return statistics;
    }
    
    @Override
    public Map<String, Object> getAllGroupLeaderStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总团长数
        Long totalLeaders = this.count();
        statistics.put("totalLeaders", totalLeaders);
        
        // 各状态团长数
        QueryWrapper<GroupLeader> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("status", "PENDING");
        Long pendingLeaders = this.count(pendingWrapper);
        statistics.put("pendingLeaders", pendingLeaders);
        
        QueryWrapper<GroupLeader> approvedWrapper = new QueryWrapper<>();
        approvedWrapper.eq("status", "APPROVED");
        Long approvedLeaders = this.count(approvedWrapper);
        statistics.put("approvedLeaders", approvedLeaders);
        
        QueryWrapper<GroupLeader> rejectedWrapper = new QueryWrapper<>();
        rejectedWrapper.eq("status", "REJECTED");
        Long rejectedLeaders = this.count(rejectedWrapper);
        statistics.put("rejectedLeaders", rejectedLeaders);
        
        // 今日新申请团长数
        QueryWrapper<GroupLeader> todayWrapper = new QueryWrapper<>();
        todayWrapper.ge("created_at", LocalDateTime.now().toLocalDate());
        Long todayNewLeaders = this.count(todayWrapper);
        statistics.put("todayNewLeaders", todayNewLeaders);
        
        // 本月新申请团长数
        QueryWrapper<GroupLeader> monthWrapper = new QueryWrapper<>();
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        monthWrapper.ge("created_at", monthStart);
        Long monthNewLeaders = this.count(monthWrapper);
        statistics.put("monthNewLeaders", monthNewLeaders);
        
        return statistics;
    }
    
    @Override
    public GroupLeader getByUserId(Long userId) {
        QueryWrapper<GroupLeader> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }
    
    @Override
    @Transactional
    public void updateGroupLeaderStatus(Long id, String status) {
        GroupLeader groupLeader = this.getById(id);
        if (groupLeader != null) {
            groupLeader.setStatus(status);
            groupLeader.setUpdatedAt(LocalDateTime.now());
            this.updateById(groupLeader);
        }
    }
    
    @Override
    @Transactional
    public void assignCommunity(Long leaderId, Long communityId) {
        try {
            GroupLeader groupLeader = this.getById(leaderId);
            if (groupLeader == null) {
                throw new RuntimeException("团长不存在");
            }
            
            groupLeader.setCommunityId(communityId);
            groupLeader.setUpdatedAt(LocalDateTime.now());
            
            boolean success = this.updateById(groupLeader);
            if (!success) {
                throw new RuntimeException("分配社区失败");
            }
            
            log.info("团长 {} 分配社区 {} 成功", leaderId, communityId);
        } catch (Exception e) {
            log.error("为团长 {} 分配社区 {} 失败: {}", leaderId, communityId, e.getMessage());
            throw new RuntimeException("分配社区失败: " + e.getMessage());
        }
    }
}