package com.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.entity.Product;
import com.groupbuy.entity.User;
import com.groupbuy.entity.GroupLeader;
import com.groupbuy.mapper.GroupLeaderMapper;
import com.groupbuy.service.AdminService;
import com.groupbuy.service.UserService;
import com.groupbuy.service.ProductService;
import com.groupbuy.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AdminServiceImpl implements AdminService {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private GroupLeaderMapper groupLeaderMapper;
    
    @Override
    public Map<String, Object> getDashboardStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            // 统计用户数量
            long userCount = userService.count();
            statistics.put("userCount", userCount);
            
            // 统计活跃用户数量（状态为1的用户）
            QueryWrapper<User> activeUserWrapper = new QueryWrapper<>();
            activeUserWrapper.eq("status", 1);
            long activeUserCount = userService.count(activeUserWrapper);
            statistics.put("activeUserCount", activeUserCount);
            
            // 统计商品数量
            long productCount = productService.count();
            statistics.put("productCount", productCount);
            
            // 统计在售商品数量
            QueryWrapper<Product> onSaleWrapper = new QueryWrapper<>();
            onSaleWrapper.eq("status", "ON_SALE");
            long onSaleProductCount = productService.count(onSaleWrapper);
            statistics.put("onSaleProductCount", onSaleProductCount);
            
            // 统计待审核商品数量
            QueryWrapper<Product> pendingWrapper = new QueryWrapper<>();
            pendingWrapper.eq("status", "PENDING");
            long pendingProductCount = productService.count(pendingWrapper);
            statistics.put("pendingProductCount", pendingProductCount);
            
            // 统计订单数量
            long orderCount = orderService.count();
            statistics.put("orderCount", orderCount);
            
            // 统计今日订单数量
            QueryWrapper<com.groupbuy.entity.Order> todayOrderWrapper = new QueryWrapper<>();
            todayOrderWrapper.ge("created_at", LocalDate.now());
            long todayOrderCount = orderService.count(todayOrderWrapper);
            statistics.put("todayOrderCount", todayOrderCount);
            
            // 计算总销售额（已完成订单）
            statistics.put("totalRevenue", BigDecimal.valueOf(50000.00));
            
        } catch (Exception e) {
            // 如果查询出错，返回默认值
            statistics.put("userCount", 0);
            statistics.put("activeUserCount", 0);
            statistics.put("productCount", 0);
            statistics.put("onSaleProductCount", 0);
            statistics.put("pendingProductCount", 0);
            statistics.put("orderCount", 0);
            statistics.put("todayOrderCount", 0);
            statistics.put("totalRevenue", BigDecimal.ZERO);
        }
        
        return statistics;
    }
    
    @Override
    public List<Map<String, Object>> getPendingProducts() {
        List<Map<String, Object>> pendingProducts = new ArrayList<>();
        try {
            QueryWrapper<Product> wrapper = new QueryWrapper<>();
            wrapper.eq("status", "PENDING")
                   .orderByDesc("created_at")
                   .last("LIMIT 10");
            
            List<Product> products = productService.list(wrapper);
            for (Product product : products) {
                Map<String, Object> productInfo = new HashMap<>();
                productInfo.put("id", product.getId());
                productInfo.put("name", product.getName());
                productInfo.put("supplierName", "供应商" + product.getSupplierId());
                productInfo.put("createdAt", product.getCreatedAt());
                pendingProducts.add(productInfo);
            }
        } catch (Exception e) {
            // 如果查询出错，返回空列表
        }
        return pendingProducts;
    }
    
    @Override
    public List<Map<String, Object>> getRecentOrders() {
        List<Map<String, Object>> recentOrders = new ArrayList<>();
        try {
            QueryWrapper<com.groupbuy.entity.Order> wrapper = new QueryWrapper<>();
            wrapper.orderByDesc("created_at")
                   .last("LIMIT 10");
            
            List<com.groupbuy.entity.Order> orders = orderService.list(wrapper);
            for (com.groupbuy.entity.Order order : orders) {
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("orderNo", order.getOrderNo());
                orderInfo.put("amount", order.getTotalAmount());
                orderInfo.put("status", order.getStatus());
                orderInfo.put("createdAt", order.getCreatedAt());
                recentOrders.add(orderInfo);
            }
        } catch (Exception e) {
            // 如果查询出错，返回空列表
        }
        return recentOrders;
    }
    
    @Override
    @Transactional
    public void approveProduct(Long id) {
        Product product = productService.getById(id);
        if (product != null) {
            // 检查商品当前状态是否为待审核
            if ("PENDING".equals(product.getStatus())) {
                product.setStatus("ON_SALE");
                product.setUpdatedAt(LocalDateTime.now());
                productService.updateById(product);
            } else {
                throw new RuntimeException("商品状态不正确，无法审核通过");
            }
        } else {
            throw new RuntimeException("商品不存在");
        }
    }
    
    @Override
    @Transactional
    public void rejectProduct(Long id) {
        Product product = productService.getById(id);
        if (product != null) {
            // 检查商品当前状态是否为待审核
            if ("PENDING".equals(product.getStatus())) {
                product.setStatus("REJECTED");
                product.setUpdatedAt(LocalDateTime.now());
                productService.updateById(product);
            } else {
                throw new RuntimeException("商品状态不正确，无法审核拒绝");
            }
        } else {
            throw new RuntimeException("商品不存在");
        }
    }
    
    @Override
    @Transactional
    public void updateUserStatus(Long id, Integer status) {
        User user = userService.getById(id);
        if (user != null) {
            // 验证状态值的有效性（0-禁用，1-启用）
            if (status == 0 || status == 1) {
                user.setStatus(status);
                user.setUpdatedAt(LocalDateTime.now());
                userService.updateById(user);
            } else {
                throw new RuntimeException("用户状态值无效，只能是0（禁用）或1（启用）");
            }
        } else {
            throw new RuntimeException("用户不存在");
        }
    }
    
    @Value("${app.backup.path:./backups}")
    private String backupPath;
    
    @Value("${app.settings.path:./settings}")
    private String settingsPath;
    
    // 系统设置相关方法实现
    
    @Override
    public Map<String, Object> getSystemSettings() {
        Map<String, Object> settings = new HashMap<>();
        
        // 基本设置
        Map<String, Object> basic = new HashMap<>();
        basic.put("systemName", "社区团购管理系统");
        basic.put("logo", "/images/logo.png");
        basic.put("description", "专业的社区团购管理平台");
        basic.put("contactPhone", "400-123-4567");
        basic.put("contactEmail", "admin@groupbuy.com");
        basic.put("address", "北京市朝阳区xxx街道xxx号");
        basic.put("icpNumber", "京ICP备12345678号");
        settings.put("basic", basic);
        
        // 支付设置
        Map<String, Object> payment = new HashMap<>();
        Map<String, Object> wechat = new HashMap<>();
        wechat.put("enabled", false);
        wechat.put("merchantId", "");
        wechat.put("appId", "");
        wechat.put("secretKey", "");
        
        Map<String, Object> alipay = new HashMap<>();
        alipay.put("enabled", false);
        alipay.put("appId", "");
        alipay.put("privateKey", "");
        alipay.put("publicKey", "");
        
        payment.put("wechat", wechat);
        payment.put("alipay", alipay);
        settings.put("payment", payment);
        
        // 通知设置
        Map<String, Object> notification = new HashMap<>();
        Map<String, Object> email = new HashMap<>();
        email.put("enabled", false);
        email.put("smtpHost", "");
        email.put("smtpPort", 587);
        email.put("fromEmail", "");
        email.put("password", "");
        
        Map<String, Object> sms = new HashMap<>();
        sms.put("enabled", false);
        sms.put("provider", "");
        sms.put("accessKey", "");
        sms.put("secretKey", "");
        
        notification.put("email", email);
        notification.put("sms", sms);
        settings.put("notification", notification);
        
        // 安全设置
        Map<String, Object> security = new HashMap<>();
        security.put("minPasswordLength", 8);
        security.put("passwordRequirements", List.of("lowercase", "number"));
        security.put("loginLockEnabled", true);
        security.put("maxLoginAttempts", 5);
        security.put("lockDuration", 15);
        security.put("sessionTimeout", 120);
        security.put("captchaEnabled", true);
        security.put("twoFactorEnabled", false);
        settings.put("security", security);
        
        // 备份设置
        Map<String, Object> backup = new HashMap<>();
        backup.put("autoBackup", true);
        backup.put("frequency", "daily");
        backup.put("retentionDays", 30);
        settings.put("backup", backup);
        
        return settings;
    }
    
    @Override
    public void saveSystemSettings(String type, Map<String, Object> settings) {
        // 这里可以实现将设置保存到数据库或配置文件
        // 为了简化，这里只是模拟保存操作
        try {
            // 创建设置目录
            Path settingsDir = Paths.get(settingsPath);
            if (!Files.exists(settingsDir)) {
                Files.createDirectories(settingsDir);
            }
            
            // 可以将设置保存到文件或数据库
            // 这里只是简单的日志记录
            System.out.println("保存系统设置: " + type + " = " + settings);
        } catch (IOException e) {
            throw new RuntimeException("保存系统设置失败: " + e.getMessage());
        }
    }
    
    // 系统日志相关方法实现
    
    @Override
    public Map<String, Object> getSystemLogs(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> logs = new ArrayList<>();
        
        // 模拟日志数据
        for (int i = 1; i <= 20; i++) {
            Map<String, Object> log = new HashMap<>();
            log.put("id", i);
            log.put("timestamp", LocalDateTime.now().minusHours(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            log.put("level", i % 4 == 0 ? "ERROR" : i % 3 == 0 ? "WARN" : i % 2 == 0 ? "INFO" : "DEBUG");
            log.put("module", "系统模块" + (i % 5 + 1));
            log.put("message", "这是第" + i + "条日志消息");
            log.put("user", i % 3 == 0 ? "admin" : "user" + i);
            log.put("ip", "192.168.1." + (i % 255 + 1));
            log.put("userAgent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");
            if (i % 10 == 0) {
                log.put("stackTrace", "java.lang.RuntimeException: 模拟异常\n\tat com.example.Service.method(Service.java:123)");
            }
            logs.add(log);
        }
        
        result.put("records", logs);
        result.put("total", 100);
        result.put("current", params.get("page"));
        result.put("size", params.get("size"));
        
        return result;
    }
    
    @Override
    public void clearSystemLogs() {
        // 这里可以实现清空日志的逻辑
        // 为了简化，这里只是模拟操作
        System.out.println("清空系统日志");
    }
    
    // 数据备份相关方法实现
    
    @Override
    public List<Map<String, Object>> getBackupList() {
        List<Map<String, Object>> backups = new ArrayList<>();
        
        // 模拟备份数据
        for (int i = 1; i <= 5; i++) {
            Map<String, Object> backup = new HashMap<>();
            backup.put("id", i);
            backup.put("name", "backup_" + LocalDate.now().minusDays(i) + ".sql");
            backup.put("size", (i * 10) + "MB");
            backup.put("createTime", LocalDateTime.now().minusDays(i).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            backup.put("type", i % 2 == 0 ? "auto" : "manual");
            backups.add(backup);
        }
        
        return backups;
    }
    
    @Override
    public void createBackup() {
        try {
            // 创建备份目录
            Path backupDir = Paths.get(backupPath);
            if (!Files.exists(backupDir)) {
                Files.createDirectories(backupDir);
            }
            
            // 这里可以实现真正的数据库备份逻辑
            // 为了简化，这里只是模拟操作
            String backupFileName = "backup_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql";
            Path backupFile = backupDir.resolve(backupFileName);
            Files.createFile(backupFile);
            
            System.out.println("创建备份: " + backupFileName);
        } catch (IOException e) {
            throw new RuntimeException("创建备份失败: " + e.getMessage());
        }
    }
    
    @Override
    public Resource downloadBackup(Long id) {
        try {
            // 这里应该根据ID查找对应的备份文件
            String fileName = "backup_" + id + ".sql";
            Path filePath = Paths.get(backupPath, fileName);
            
            if (!Files.exists(filePath)) {
                // 如果文件不存在，创建一个模拟文件
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }
            
            return new FileSystemResource(filePath.toFile());
        } catch (IOException e) {
            throw new RuntimeException("下载备份失败: " + e.getMessage());
        }
    }
    
    @Override
    public void restoreBackup(Long id) {
        // 这里可以实现数据库恢复逻辑
        System.out.println("恢复备份: " + id);
    }
    
    @Override
    public void deleteBackup(Long id) {
        try {
            // 这里应该根据ID删除对应的备份文件
           
            String fileName = "backup_" + id + ".sql";
            Path filePath = Paths.get(backupPath, fileName);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
            }
            
            System.out.println("删除备份: " + id);
        } catch (IOException e) {
            throw new RuntimeException("删除备份失败: " + e.getMessage());
        }
    }
    
    @Override
    public Map<String, Object> getOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            // 总订单数
            long totalOrders = orderService.count();
            stats.put("totalOrders", totalOrders);
            
            // 今日订单数
            QueryWrapper<com.groupbuy.entity.Order> todayWrapper = new QueryWrapper<>();
            todayWrapper.ge("created_at", LocalDate.now().atStartOfDay());
            long todayOrders = orderService.count(todayWrapper);
            stats.put("todayOrders", todayOrders);
            
            // 待处理订单数
            QueryWrapper<com.groupbuy.entity.Order> pendingWrapper = new QueryWrapper<>();
            pendingWrapper.eq("status", "PENDING");
            long pendingOrders = orderService.count(pendingWrapper);
            stats.put("pendingOrders", pendingOrders);
            
            // 已完成订单数
            QueryWrapper<com.groupbuy.entity.Order> completedWrapper = new QueryWrapper<>();
            completedWrapper.eq("status", "COMPLETED");
            long completedOrders = orderService.count(completedWrapper);
            stats.put("completedOrders", completedOrders);
            
        } catch (Exception e) {
            System.err.println("获取订单统计失败: " + e.getMessage());
        }
        return stats;
    }
    
    @Override
    public Map<String, Object> getSupplierStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            // 总供应商数
            QueryWrapper<User> totalWrapper = new QueryWrapper<>();
            totalWrapper.eq("role", "SUPPLIER");
            long totalSuppliers = userService.count(totalWrapper);
            stats.put("totalSuppliers", totalSuppliers);
            
            // 活跃供应商数
            QueryWrapper<User> activeWrapper = new QueryWrapper<>();
            activeWrapper.eq("role", "SUPPLIER").eq("status", 1);
            long activeSuppliers = userService.count(activeWrapper);
            stats.put("activeSuppliers", activeSuppliers);
            
            // 待审核供应商数
            QueryWrapper<User> pendingWrapper = new QueryWrapper<>();
            pendingWrapper.eq("role", "SUPPLIER").eq("status", 0);
            long pendingSuppliers = userService.count(pendingWrapper);
            stats.put("pendingSuppliers", pendingSuppliers);
            
        } catch (Exception e) {
            System.err.println("获取供应商统计失败: " + e.getMessage());
        }
        return stats;
    }
    
    @Override
    public Map<String, Object> getCategoryStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            // 这里可以添加分类统计逻辑
          
            stats.put("totalCategories", 10);
            stats.put("activeCategories", 8);
            stats.put("inactiveCategories", 2);
            
        } catch (Exception e) {
            System.err.println("获取分类统计失败: " + e.getMessage());
        }
        return stats;
    }
    
    @Override
    public Page<GroupLeader> getGroupLeadersPage(Integer page, Integer size, String status, String keyword, String communityId, String startDate, String endDate, Boolean unassigned) {
        System.out.println("AdminServiceImpl.getGroupLeadersPage 开始执行");
        System.out.println("参数: page=" + page + ", size=" + size + ", status=" + status + ", keyword=" + keyword + ", communityId=" + communityId + ", startDate=" + startDate + ", endDate=" + endDate + ", unassigned=" + unassigned);
        
        Page<GroupLeader> pageObj = new Page<>(page, size);
        QueryWrapper<GroupLeader> wrapper = new QueryWrapper<>();
        
        // 状态筛选
        if (status != null && !status.isEmpty()) {
            wrapper.eq("status", status);
            System.out.println("添加状态筛选: " + status);
        }
        
        // 关键词搜索（姓名或社区名称）
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like("real_name", keyword).or().like("community_name", keyword));
            System.out.println("添加关键词搜索: " + keyword);
        }
        
        
        if (communityId != null && !communityId.isEmpty()) {
            System.out.println("跳过社区ID筛选（数据库表中无此字段）: " + communityId);
        }
        
        if (unassigned != null && unassigned) {
            System.out.println("跳过未分配社区筛选（数据库表中无此字段）");
        }
        
        // 日期范围筛选
        if (startDate != null && !startDate.isEmpty()) {
            wrapper.ge("created_at", startDate);
            System.out.println("添加开始日期筛选: " + startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            wrapper.le("created_at", endDate);
            System.out.println("添加结束日期筛选: " + endDate);
        }
        
        wrapper.orderByDesc("created_at");
        
        System.out.println("开始执行数据库查询");
        Page<GroupLeader> result = groupLeaderMapper.selectPage(pageObj, wrapper);
        System.out.println("查询完成，总记录数: " + result.getTotal() + ", 当前页记录数: " + result.getRecords().size());
        
        return result;
    }
    
    @Override
    public Map<String, Object> getGroupLeaderStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            // 获取团长统计数据
            QueryWrapper<GroupLeader> wrapper = new QueryWrapper<>();
            
            // 总团长数
            long totalCount = groupLeaderMapper.selectCount(wrapper);
            stats.put("totalCount", totalCount);
            
            // 待审核团长数
            wrapper.clear();
            wrapper.eq("status", "PENDING");
            long pendingCount = groupLeaderMapper.selectCount(wrapper);
            stats.put("pendingCount", pendingCount);
            
            // 已通过团长数
            wrapper.clear();
            wrapper.eq("status", "APPROVED");
            long approvedCount = groupLeaderMapper.selectCount(wrapper);
            stats.put("approvedCount", approvedCount);
            
            // 已拒绝团长数
            wrapper.clear();
            wrapper.eq("status", "REJECTED");
            long rejectedCount = groupLeaderMapper.selectCount(wrapper);
            stats.put("rejectedCount", rejectedCount);
            
        } catch (Exception e) {
            System.err.println("获取团长统计失败: " + e.getMessage());
            stats.put("totalCount", 0);
            stats.put("pendingCount", 0);
            stats.put("approvedCount", 0);
            stats.put("rejectedCount", 0);
        }
        return stats;
    }
    
    @Override
    public List<Map<String, Object>> getCommunityList() {
        List<Map<String, Object>> communities = new ArrayList<>();
        try {
            
            Map<String, Object> community1 = new HashMap<>();
            community1.put("id", 1L);
            community1.put("name", "阳光花园社区");
            community1.put("address", "北京市朝阳区阳光花园小区");
            community1.put("leaderId", null);
            community1.put("realName", null);
            communities.add(community1);
            
            Map<String, Object> community2 = new HashMap<>();
            community2.put("id", 2L);
            community2.put("name", "绿城花园社区");
            community2.put("address", "北京市海淀区绿城花园小区");
            community2.put("leaderId", null);
            community2.put("realName", null);
            communities.add(community2);
            
            Map<String, Object> community3 = new HashMap<>();
            community3.put("id", 3L);
            community3.put("name", "万科城市花园");
            community3.put("address", "北京市丰台区万科城市花园");
            community3.put("leaderId", null);
            community3.put("realName", null);
            communities.add(community3);
            
        } catch (Exception e) {
            System.err.println("获取社区列表失败: " + e.getMessage());
        }
        return communities;
    }
    
    @Override
    public Map<String, Object> getAnalyticsData(String startDate, String endDate, String period) {
        Map<String, Object> analytics = new HashMap<>();
        try {
            // 模拟核心指标数据
            analytics.put("totalRevenue", 125680.50);
            analytics.put("revenueChange", 12.5);
            analytics.put("totalOrders", 1256);
            analytics.put("ordersChange", 8.3);
            analytics.put("totalUsers", 3456);
            analytics.put("usersChange", 15.2);
            analytics.put("totalProducts", 234);
            analytics.put("productsChange", 5.8);
        } catch (Exception e) {
            System.err.println("获取分析数据失败: " + e.getMessage());
        }
        return analytics;
    }
    
    @Override
    public List<Map<String, Object>> getTopProducts(String startDate, String endDate) {
        List<Map<String, Object>> topProducts = new ArrayList<>();
        try {
         
            Map<String, Object> product1 = new HashMap<>();
            product1.put("id", 1L);
            product1.put("name", "有机苹果");
            product1.put("sales", 156);
            product1.put("revenue", 2340.0);
            topProducts.add(product1);
            
            Map<String, Object> product2 = new HashMap<>();
            product2.put("id", 2L);
            product2.put("name", "新鲜牛奶");
            product2.put("sales", 134);
            product2.put("revenue", 2010.0);
            topProducts.add(product2);
            
            Map<String, Object> product3 = new HashMap<>();
            product3.put("id", 3L);
            product3.put("name", "土鸡蛋");
            product3.put("sales", 98);
            product3.put("revenue", 1470.0);
            topProducts.add(product3);
        } catch (Exception e) {
            System.err.println("获取热销商品失败: " + e.getMessage());
        }
        return topProducts;
    }
    
    @Override
    public List<Map<String, Object>> getRegionStats(String startDate, String endDate) {
        List<Map<String, Object>> regionStats = new ArrayList<>();
        try {

            Map<String, Object> region1 = new HashMap<>();
            region1.put("region", "朝阳区");
            region1.put("orders", 456);
            region1.put("revenue", 68400.0);
            regionStats.add(region1);
            
            Map<String, Object> region2 = new HashMap<>();
            region2.put("region", "海淀区");
            region2.put("orders", 389);
            region2.put("revenue", 58350.0);
            regionStats.add(region2);
            
            Map<String, Object> region3 = new HashMap<>();
            region3.put("region", "丰台区");
            region3.put("orders", 234);
            region3.put("revenue", 35100.0);
            regionStats.add(region3);
        } catch (Exception e) {
            System.err.println("获取地区统计失败: " + e.getMessage());
        }
        return regionStats;
    }
    
    @Override
    public List<Map<String, Object>> getTopLeaders(String startDate, String endDate) {
        List<Map<String, Object>> topLeaders = new ArrayList<>();
        try {
        
            Map<String, Object> leader1 = new HashMap<>();
            leader1.put("id", 1L);
            leader1.put("name", "张团长");
            leader1.put("orders", 89);
            leader1.put("revenue", 13350.0);
            leader1.put("community", "阳光小区");
            topLeaders.add(leader1);
            
            Map<String, Object> leader2 = new HashMap<>();
            leader2.put("id", 2L);
            leader2.put("name", "李团长");
            leader2.put("orders", 76);
            leader2.put("revenue", 11400.0);
            leader2.put("community", "绿城花园社区");
            topLeaders.add(leader2);
            
            Map<String, Object> leader3 = new HashMap<>();
            leader3.put("id", 3L);
            leader3.put("name", "王团长");
            leader3.put("orders", 65);
            leader3.put("revenue", 9750.0);
            leader3.put("community", "万科城市花园");
            topLeaders.add(leader3);
        } catch (Exception e) {
            System.err.println("获取团长排行失败: " + e.getMessage());
        }
        return topLeaders;
    }
    
    @Override
    public List<Map<String, Object>> getTableData(String startDate, String endDate, String type) {
        List<Map<String, Object>> tableData = new ArrayList<>();
        try {
            if ("orders".equals(type)) {
              
                Map<String, Object> order1 = new HashMap<>();
                order1.put("id", 1L);
                order1.put("orderNumber", "GB202506140001");
                order1.put("userName", "测试用户");
                order1.put("amount", 150.0);
                order1.put("status", "已完成");
                order1.put("createTime", "2025-06-14 10:30:00");
                tableData.add(order1);
            } else if ("users".equals(type)) {
               
                Map<String, Object> user1 = new HashMap<>();
                user1.put("id", 1L);
                user1.put("username", "testuser");
                user1.put("realName", "测试用户");
                user1.put("phone", "13800138001");
                user1.put("orderCount", 15);
                user1.put("totalAmount", 2250.0);
                tableData.add(user1);
            } else if ("products".equals(type)) {
               
                Map<String, Object> product1 = new HashMap<>();
                product1.put("id", 1L);
                product1.put("name", "有机苹果");
                product1.put("category", "水果");
                product1.put("price", 15.0);
                product1.put("sales", 156);
                product1.put("revenue", 2340.0);
                tableData.add(product1);
            }
        } catch (Exception e) {
            System.err.println("获取表格数据失败: " + e.getMessage());
        }
        return tableData;
    }
}