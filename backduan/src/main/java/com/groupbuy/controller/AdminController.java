package com.groupbuy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.common.Result;
import com.groupbuy.entity.Product;
import com.groupbuy.entity.User;
import com.groupbuy.entity.Order;
import com.groupbuy.entity.GroupLeader;
import com.groupbuy.service.AdminService;
import com.groupbuy.service.ProductService;
import com.groupbuy.service.UserService;
import com.groupbuy.service.OrderService;
import com.groupbuy.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:8080")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @GetMapping("/dashboard")
    public Result<Map<String, Object>> getDashboardData(@RequestHeader("Authorization") String token) {
        try {
            // 验证管理员权限
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Map<String, Object> statistics = adminService.getDashboardStatistics();
            
            // 构造前端期望的数据结构
            Map<String, Object> response = new HashMap<>();
            response.put("stats", statistics);
            response.put("pendingProducts", adminService.getPendingProducts());
            response.put("recentOrders", adminService.getRecentOrders());
            
            return Result.success(response);
        } catch (Exception e) {
            return Result.error("获取仪表板数据失败: " + e.getMessage());
        }
    }
    
    // 商品管理相关接口
    
    @GetMapping("/products")
    public Result<Page<Product>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String token) {
        
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Page<Product> productPage = new Page<>(page, size);
            QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.like("name", keyword)
                           .or()
                           .like("description", keyword);
            }
            
            if (status != null && !status.trim().isEmpty()) {
                queryWrapper.eq("status", status);
            }
            
            queryWrapper.orderByDesc("created_at");
            
            Page<Product> result = productService.page(productPage, queryWrapper);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取商品列表失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/products/{productId}/status")
    public Result<String> updateProductStatus(
            @PathVariable Long productId,
            @RequestParam String status,
            @RequestHeader("Authorization") String token) {
        
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Product product = productService.getById(productId);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            product.setStatus(status);
            productService.updateById(product);
            
            return Result.success("商品状态更新成功");
        } catch (Exception e) {
            return Result.error("更新商品状态失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/product/approve/{id}")
    public Result<Void> approveProduct(@PathVariable Long id) {
        adminService.approveProduct(id);
        return Result.success();
    }
    
    @PutMapping("/product/reject/{id}")
    public Result<Void> rejectProduct(@PathVariable Long id) {
        adminService.rejectProduct(id);
        return Result.success();
    }
    
    // 用户管理相关接口
    
    @GetMapping("/users")
    public Result<Page<User>> getUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestHeader("Authorization") String token) {
        
        try {
            String actualToken = token.replace("Bearer ", "");
            String userRole = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(userRole)) {
                return Result.error("权限不足");
            }
            
            Page<User> userPage = new Page<>(page, size);
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.like("username", keyword)
                           .or()
                           .like("email", keyword)
                           .or()
                           .like("phone", keyword);
            }
            
            if (role != null && !role.trim().isEmpty()) {
                queryWrapper.eq("role", role);
            }
            
            queryWrapper.orderByDesc("created_at");
            
            Page<User> result = userService.page(userPage, queryWrapper);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/users")
    public Result<String> createUser(@RequestBody User user, @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            // 检查用户名是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", user.getUsername());
            if (userService.count(queryWrapper) > 0) {
                return Result.error("用户名已存在");
            }
            
            userService.save(user);
            return Result.success("用户创建成功");
        } catch (Exception e) {
            return Result.error("创建用户失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/users/{userId}")
    public Result<String> updateUser(
            @PathVariable Long userId,
            @RequestBody User user,
            @RequestHeader("Authorization") String token) {
        
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            User existingUser = userService.getById(userId);
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            
            user.setId(userId);
            userService.updateById(user);
            
            return Result.success("用户更新成功");
        } catch (Exception e) {
            return Result.error("更新用户失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/users/{userId}")
    public Result<String> deleteUser(@PathVariable Long userId, @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            userService.removeById(userId);
            return Result.success("用户删除成功");
        } catch (Exception e) {
            return Result.error("删除用户失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/user/status/{id}")
    public Result<Void> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.updateUserStatus(id, status);
        return Result.success();
    }
    
    @PutMapping("/users/{userId}/status")
    public Result<String> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam Integer status,
            @RequestHeader("Authorization") String token) {
        
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            User user = userService.getById(userId);
            if (user == null) {
                return Result.error("用户不存在");
            }
            
            user.setStatus(status);
            userService.updateById(user);
            
            return Result.success("用户状态更新成功");
        } catch (Exception e) {
            return Result.error("更新用户状态失败: " + e.getMessage());
        }
    }
    
    // 订单管理相关接口
    @GetMapping("/orders")
    public Result<Page<Order>> getOrders(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestHeader("Authorization") String token) {
        
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Page<Order> orderPage = new Page<>(page, size);
            QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
            
            if (keyword != null && !keyword.trim().isEmpty()) {
                queryWrapper.like("order_no", keyword);
            }
            
            if (status != null && !status.trim().isEmpty()) {
                queryWrapper.eq("status", status);
            }
            
            queryWrapper.orderByDesc("created_at");
            
            Page<Order> result = orderService.page(orderPage, queryWrapper);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取订单列表失败: " + e.getMessage());
        }
    }
    
    @PutMapping("/orders/{orderId}/status")
    public Result<String> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            String status = (String) request.get("status");
            if (status == null || status.trim().isEmpty()) {
                return Result.error("状态不能为空");
            }
            
            Order order = orderService.getById(orderId);
            if (order == null) {
                return Result.error("订单不存在");
            }
            
            order.setStatus(status);
            orderService.updateById(order);
            
            return Result.success("订单状态更新成功");
        } catch (Exception e) {
            return Result.error("更新订单状态失败: " + e.getMessage());
        }
    }
    
    // 统计相关接口
    @GetMapping("/orders/stats")
    public Result<Map<String, Object>> getOrderStats(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Map<String, Object> stats = adminService.getOrderStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取订单统计失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/suppliers/stats")
    public Result<Map<String, Object>> getSupplierStats(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Map<String, Object> stats = adminService.getSupplierStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取供应商统计失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/categories/stats")
    public Result<Map<String, Object>> getCategoryStats(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Map<String, Object> stats = adminService.getCategoryStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取分类统计失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/group-leaders/page")
    public Result<Page<GroupLeader>> getGroupLeadersPage(
            @RequestHeader("Authorization") String token,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String communityId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) Boolean unassigned) {
        System.out.println("收到团长分页查询请求: page=" + page + ", size=" + size + ", status=" + status + ", keyword=" + keyword + ", communityId=" + communityId + ", unassigned=" + unassigned);
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                System.out.println("权限验证失败，当前角色: " + role);
                return Result.error("权限不足");
            }
            
            System.out.println("开始调用adminService.getGroupLeadersPage");
            Page<GroupLeader> leaders = adminService.getGroupLeadersPage(page, size, status, keyword, communityId, startDate, endDate, unassigned);
            System.out.println("查询结果: 总记录数=" + leaders.getTotal() + ", 当前页记录数=" + leaders.getRecords().size());
            return Result.success(leaders);
        } catch (Exception e) {
            System.err.println("获取团长列表异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取团长列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/group-leaders/stats")
    public Result<Map<String, Object>> getGroupLeaderStats(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Map<String, Object> stats = adminService.getGroupLeaderStats();
            return Result.success(stats);
        } catch (Exception e) {
            return Result.error("获取团长统计失败: " + e.getMessage());
        }
    }
    
    // 系统设置相关接口
    
    @GetMapping("/settings")
    public Result<Map<String, Object>> getSystemSettings(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Map<String, Object> settings = adminService.getSystemSettings();
            return Result.success(settings);
        } catch (Exception e) {
            return Result.error("获取系统设置失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/settings/{type}")
    public Result<Void> saveSystemSettings(
            @PathVariable String type,
            @RequestBody Map<String, Object> settings,
            @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            adminService.saveSystemSettings(type, settings);
            return Result.success();
        } catch (Exception e) {
            return Result.error("保存系统设置失败: " + e.getMessage());
        }
    }
    
    // 系统日志相关接口
    @GetMapping("/logs")
    public Result<Map<String, Object>> getSystemLogs(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String date,
            @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Map<String, Object> params = new HashMap<>();
            params.put("page", page);
            params.put("size", size);
            params.put("level", level);
            params.put("date", date);
            
            Map<String, Object> logs = adminService.getSystemLogs(params);
            return Result.success(logs);
        } catch (Exception e) {
            return Result.error("获取系统日志失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/logs")
    public Result<Void> clearSystemLogs(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            adminService.clearSystemLogs();
            return Result.success();
        } catch (Exception e) {
            return Result.error("清空系统日志失败: " + e.getMessage());
        }
    }
    
    // 数据备份相关接口  
    @GetMapping("/backups")
    public Result<List<Map<String, Object>>> getBackupList(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            List<Map<String, Object>> backups = adminService.getBackupList();
            return Result.success(backups);
        } catch (Exception e) {
            return Result.error("获取备份列表失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/backup")
    public Result<Void> createBackup(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            adminService.createBackup();
            return Result.success();
        } catch (Exception e) {
            return Result.error("创建备份失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/backup/download/{id}")
    public ResponseEntity<Resource> downloadBackup(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return ResponseEntity.status(403).build();
            }
            
            Resource resource = adminService.downloadBackup(id);
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @PostMapping("/backup/restore/{id}")
    public Result<Void> restoreBackup(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            adminService.restoreBackup(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("恢复备份失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/backup/{id}")
    public Result<Void> deleteBackup(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            adminService.deleteBackup(id);
            return Result.success();
        } catch (Exception e) {
            return Result.error("删除备份失败: " + e.getMessage());
        }
    }
    
    // 社区管理相关接口
    @GetMapping("/communities")
    public Result<List<Map<String, Object>>> getCommunityList(@RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            List<Map<String, Object>> communities = adminService.getCommunityList();
            return Result.success(communities);
        } catch (Exception e) {
            return Result.error("获取社区列表失败: " + e.getMessage());
        }
    }
    
    // 数据分析相关接口
    @GetMapping("/analytics")
    public Result<Map<String, Object>> getAnalyticsData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String period,
            @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Map<String, Object> analytics = adminService.getAnalyticsData(startDate, endDate, period);
            return Result.success(analytics);
        } catch (Exception e) {
            return Result.error("获取分析数据失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/analytics/products/top")
    public Result<List<Map<String, Object>>> getTopProducts(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            List<Map<String, Object>> topProducts = adminService.getTopProducts(startDate, endDate);
            return Result.success(topProducts);
        } catch (Exception e) {
            return Result.error("获取热销商品失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/analytics/regions")
    public Result<List<Map<String, Object>>> getRegionStats(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            List<Map<String, Object>> regionStats = adminService.getRegionStats(startDate, endDate);
            return Result.success(regionStats);
        } catch (Exception e) {
            return Result.error("获取地区统计失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/analytics/leaders/top")
    public Result<List<Map<String, Object>>> getTopLeaders(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            List<Map<String, Object>> topLeaders = adminService.getTopLeaders(startDate, endDate);
            return Result.success(topLeaders);
        } catch (Exception e) {
            return Result.error("获取团长排行失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/analytics/table")
    public Result<List<Map<String, Object>>> getTableData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String type,
            @RequestHeader("Authorization") String token) {
        try {
            String actualToken = token.replace("Bearer ", "");
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            List<Map<String, Object>> tableData = adminService.getTableData(startDate, endDate, type);
            return Result.success(tableData);
        } catch (Exception e) {
            return Result.error("获取表格数据失败: " + e.getMessage());
        }
    }
}