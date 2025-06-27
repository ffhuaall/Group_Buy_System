package com.groupbuy.service;

import java.util.Map;
import java.util.List;
import org.springframework.core.io.Resource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.groupbuy.entity.GroupLeader;

public interface AdminService {
    Map<String, Object> getDashboardStatistics();
    List<Map<String, Object>> getPendingProducts();
    List<Map<String, Object>> getRecentOrders();
    void approveProduct(Long id);
    void rejectProduct(Long id);
    void updateUserStatus(Long id, Integer status);
    
    // 系统设置相关方法
    Map<String, Object> getSystemSettings();
    void saveSystemSettings(String type, Map<String, Object> settings);
    
    // 系统日志相关方法
    Map<String, Object> getSystemLogs(Map<String, Object> params);
    void clearSystemLogs();
    
    // 数据备份相关方法
    List<Map<String, Object>> getBackupList();
    void createBackup();
    Resource downloadBackup(Long id);
    void restoreBackup(Long id);
    void deleteBackup(Long id);
    
    // 统计相关方法
    Map<String, Object> getOrderStats();
    Map<String, Object> getSupplierStats();
    Map<String, Object> getCategoryStats();
    Map<String, Object> getGroupLeaderStats();
    Page<GroupLeader> getGroupLeadersPage(Integer page, Integer size, String status, String keyword, String communityId, String startDate, String endDate, Boolean unassigned);
    
    // 社区管理相关方法
    List<Map<String, Object>> getCommunityList();
    
    // 数据分析相关方法
    Map<String, Object> getAnalyticsData(String startDate, String endDate, String period);
    List<Map<String, Object>> getTopProducts(String startDate, String endDate);
    List<Map<String, Object>> getRegionStats(String startDate, String endDate);
    List<Map<String, Object>> getTopLeaders(String startDate, String endDate);
    List<Map<String, Object>> getTableData(String startDate, String endDate, String type);
}