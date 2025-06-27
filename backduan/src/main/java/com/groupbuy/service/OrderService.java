package com.groupbuy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.groupbuy.dto.CreateOrderRequest;
import com.groupbuy.entity.Order;

import java.util.List;
import java.util.Map;

public interface OrderService extends IService<Order> {
    
    /**
     * 创建订单
     */
    Order createOrder(CreateOrderRequest request, Long userId);
    
    /**
     * 支付订单
     */
    boolean payOrder(String orderNo, String paymentMethod);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(String orderNo, Long userId);
    
    /**
     * 确认收货
     */
    boolean confirmReceipt(String orderNo, Long userId);
    
    /**
     * 根据订单ID获取订单详情（包含订单项）
     */
    Order getOrderWithItems(Long orderId);
    
    /**
     * 确认收货
     */
    boolean confirmDelivery(String orderNo, Long userId);
    
    /**
     * 发货
     */
    boolean shipOrder(String orderNo);
    
    /**
     * 获取用户订单列表
     */
    Page<Order> getUserOrders(Long userId, String status, Integer current, Integer size);
    
    /**
     * 根据订单号查询订单
     */
    Order getOrderByOrderNo(String orderNo);
    
    /**
     * 生成订单号
     */
    String generateOrderNo();
    
    /**
     * 获取团长订单列表
     */
    Page<Order> getGroupLeaderOrders(Long groupLeaderId, String status, Integer current, Integer size);
    
    /**
     * 获取团长订单统计
     */
    Map<String, Object> getGroupLeaderOrderStatistics(Long groupLeaderId);
    
    /**
     * 获取供应商订单列表
     */
    Page<Order> getSupplierOrders(Long supplierId, String status, Integer current, Integer size);
    
    /**
     * 获取所有订单（管理员）
     */
    Page<Order> getAllOrders(String status, String orderNo, Integer current, Integer size);
    
    /**
     * 获取订单统计信息
     */
    Map<String, Object> getOrderStatistics();
    
    /**
     * 申请退款
     */
    boolean applyRefund(String orderNo, Long userId, String reason);
    
    /**
     * 处理退款
     */
    boolean processRefund(String orderNo, String action, String remark);
    
    /**
     * 订单评价
     */
    boolean reviewOrder(String orderNo, Long userId, Integer rating, String comment);
    
    /**
     * 获取物流信息
     */
    Map<String, Object> getLogistics(String orderNo, Long userId);
    
    /**
     * 更新物流信息
     */
    boolean updateLogistics(String orderNo, Long userId, String logisticsCompany, String trackingNumber);
    
    /**
     * 批量发货
     */
    Map<String, Object> batchShip(List<String> orderNos, Long userId);
    
    /**
     * 获取导出数据
     */
    List<Map<String, Object>> getExportData(String startDate, String endDate, String status);
}