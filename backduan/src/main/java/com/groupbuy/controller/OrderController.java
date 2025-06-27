package com.groupbuy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.common.Result;
import com.groupbuy.dto.CreateOrderRequest;
import com.groupbuy.entity.Order;
import com.groupbuy.service.OrderService;
import com.groupbuy.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/order")
@CrossOrigin(origins = "http://localhost:8080")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @PostMapping("/create")
    public Result<Order> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                   @RequestHeader(value = "Authorization", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return Result.error("请先登录");
        }
        try {
            String actualToken = token.replace("Bearer ", "");
            // 验证token是否有效
            if (jwtUtils.isTokenExpired(actualToken)) {
                return Result.error("登录已过期，请重新登录");
            }
            Long userId = jwtUtils.getUserIdFromToken(actualToken);
            if (userId == null) {
                return Result.error("无效的用户信息");
            }
            Order order = orderService.createOrder(request, userId);
            return Result.success(order);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return Result.error("登录已过期，请重新登录");
        } catch (io.jsonwebtoken.JwtException e) {
            return Result.error("无效的登录凭证");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建订单失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/pay/{orderNo}")
    public Result<String> payOrder(@PathVariable String orderNo,
                                 @RequestParam String paymentMethod) {
        boolean success = orderService.payOrder(orderNo, paymentMethod);
        if (success) {
            return Result.success("支付成功");
        } else {
            return Result.error("支付失败");
        }
    }
    
    @PutMapping("/confirm/{orderNo}")
    public Result<Void> confirmReceived(@PathVariable String orderNo,
                                      @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        boolean success = orderService.confirmDelivery(orderNo, userId);
        if (success) {
            return Result.success();
        } else {
            return Result.error("确认收货失败");
        }
    }
    
    @GetMapping("/list/{userId}")
    public Result<Page<Order>> getUserOrders(@PathVariable Long userId,
                                           @RequestParam(required = false) String status,
                                           @RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size) {
        Page<Order> orders = orderService.getUserOrders(userId, status, current, size);
        return Result.success(orders);
    }
    
    @DeleteMapping("/cancel/{orderNo}")
    public Result<Void> cancelOrder(@PathVariable String orderNo,
                                   @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        boolean success = orderService.cancelOrder(orderNo, userId);
        if (success) {
            return Result.success();
        } else {
            return Result.error("取消订单失败");
        }
    }
    
    @GetMapping("/{id}")
    public Result<Order> getOrderById(@PathVariable Long id,
                                     @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        Order order = orderService.getOrderWithItems(id);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error("无权限访问该订单");
        }
        return Result.success(order);
    }
    
    @GetMapping("/detail/{orderNo}")
    public Result<Order> getOrderDetail(@PathVariable String orderNo,
                                       @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        Order order = orderService.getOrderByOrderNo(orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return Result.error("无权限查看此订单");
        }
        return Result.success(order);
    }
    
    // 团长相关订单管理
    @GetMapping("/groupleader/{groupLeaderId}")
    public Result<Page<Order>> getGroupLeaderOrders(@PathVariable Long groupLeaderId,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(defaultValue = "1") Integer current,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        Page<Order> orders = orderService.getGroupLeaderOrders(groupLeaderId, status, current, size);
        return Result.success(orders);
    }
    
    @GetMapping("/groupleader/{groupLeaderId}/statistics")
    public Result<Map<String, Object>> getGroupLeaderOrderStatistics(@PathVariable Long groupLeaderId) {
        Map<String, Object> statistics = orderService.getGroupLeaderOrderStatistics(groupLeaderId);
        return Result.success(statistics);
    }
    
    // 供应商相关订单管理
    @GetMapping("/supplier/{supplierId}")
    public Result<Page<Order>> getSupplierOrders(@PathVariable Long supplierId,
                                                @RequestParam(required = false) String status,
                                                @RequestParam(defaultValue = "1") Integer current,
                                                @RequestParam(defaultValue = "10") Integer size) {
        Page<Order> orders = orderService.getSupplierOrders(supplierId, status, current, size);
        return Result.success(orders);
    }
    
    @PutMapping("/ship/{orderNo}")
    public Result<Void> shipOrder(@PathVariable String orderNo) {
        boolean success = orderService.shipOrder(orderNo);
        if (success) {
            return Result.success();
        } else {
            return Result.error("发货失败");
        }
    }
    
    // 管理员订单管理
    @GetMapping("/admin/list")
    public Result<Page<Order>> getAllOrders(@RequestParam(required = false) String status,
                                          @RequestParam(required = false) String orderNo,
                                          @RequestParam(defaultValue = "1") Integer current,
                                          @RequestParam(defaultValue = "10") Integer size) {
        Page<Order> orders = orderService.getAllOrders(status, orderNo, current, size);
        return Result.success(orders);
    }
    
    @GetMapping("/admin/statistics")
    public Result<Map<String, Object>> getOrderStatistics() {
        Map<String, Object> statistics = orderService.getOrderStatistics();
        return Result.success(statistics);
    }
    
    // 申请退款
    @PostMapping("/refund/{orderNo}")
    public Result<Void> applyRefund(@PathVariable String orderNo,
                                   @RequestParam String reason,
                                   @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        boolean success = orderService.applyRefund(orderNo, userId, reason);
        if (success) {
            return Result.success();
        } else {
            return Result.error("申请退款失败");
        }
    }
    
    // 处理退款（管理员）
    @PutMapping("/admin/refund/{orderNo}")
    public Result<Void> processRefund(@PathVariable String orderNo,
                                     @RequestParam String action, // APPROVE, REJECT
                                     @RequestParam(required = false) String remark) {
        boolean success = orderService.processRefund(orderNo, action, remark);
        if (success) {
            return Result.success();
        } else {
            return Result.error("处理退款失败");
        }
    }
    
    // 订单评价
    @PostMapping("/review/{orderNo}")
    public Result<Void> reviewOrder(@PathVariable String orderNo,
                                   @RequestParam Integer rating,
                                   @RequestParam(required = false) String comment,
                                   @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        boolean success = orderService.reviewOrder(orderNo, userId, rating, comment);
        if (success) {
            return Result.success();
        } else {
            return Result.error("评价失败");
        }
    }
    
    // 获取物流信息
    @GetMapping("/logistics/{orderNo}")
    public Result<Map<String, Object>> getLogistics(@PathVariable String orderNo,
                                                   @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        Map<String, Object> logistics = orderService.getLogistics(orderNo, userId);
        return Result.success(logistics);
    }
    
    // 更新物流信息（供应商）
    @PutMapping("/logistics/{orderNo}")
    public Result<Void> updateLogistics(@PathVariable String orderNo,
                                       @RequestParam String logisticsCompany,
                                       @RequestParam String trackingNumber,
                                       @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        boolean success = orderService.updateLogistics(orderNo, userId, logisticsCompany, trackingNumber);
        if (success) {
            return Result.success();
        } else {
            return Result.error("更新物流信息失败");
        }
    }
    
    // 批量发货（供应商）
    @PutMapping("/batch-ship")
    public Result<Map<String, Object>> batchShip(@RequestBody List<String> orderNos,
                                                @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        Map<String, Object> result = orderService.batchShip(orderNos, userId);
        return Result.success(result);
    }
    
    // 获取订单导出数据（管理员）
    @GetMapping("/admin/export")
    public Result<List<Map<String, Object>>> exportOrders(@RequestParam(required = false) String startDate,
                                                         @RequestParam(required = false) String endDate,
                                                         @RequestParam(required = false) String status) {
        List<Map<String, Object>> exportData = orderService.getExportData(startDate, endDate, status);
        return Result.success(exportData);
    }
    
    // 获取用户当前订单（用于JWT验证的用户）
    @GetMapping("/my-orders")
    public Result<Page<Order>> getMyOrders(@RequestParam(required = false) String status,
                                         @RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestHeader("Authorization") String token) {
        String actualToken = token.replace("Bearer ", "");
        Long userId = jwtUtils.getUserIdFromToken(actualToken);
        Page<Order> orders = orderService.getUserOrders(userId, status, current, size);
        return Result.success(orders);
    }
}