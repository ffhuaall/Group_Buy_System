package com.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.groupbuy.dto.CreateOrderRequest;
import com.groupbuy.entity.Order;
import com.groupbuy.entity.OrderItem;
import com.groupbuy.entity.Product;
import com.groupbuy.mapper.OrderMapper;
import com.groupbuy.service.OrderItemService;
import com.groupbuy.service.OrderService;
import com.groupbuy.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
    
    private final OrderItemService orderItemService;
    private final ProductService productService;
    private static final AtomicLong orderCounter = new AtomicLong(1);
    
    @Override
    @Transactional
    public Order createOrder(CreateOrderRequest request, Long userId) {
        // 创建订单
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setGroupLeaderId(request.getGroupLeaderId());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setRemark(request.getRemark());
        order.setStatus("PENDING_PAYMENT");
        
        // 计算订单金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.getById(itemRequest.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }
            
            // 检查库存
            if (product.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("商品库存不足");
            }
            
            BigDecimal itemTotal = product.getGroupPrice().multiply(new BigDecimal(itemRequest.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }
        
        order.setTotalAmount(totalAmount);
        order.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        order.setActualAmount(totalAmount.subtract(order.getDiscountAmount()));
        
        // 保存订单
        this.save(order);
        
        // 创建订单详情
        for (CreateOrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            Product product = productService.getById(itemRequest.getProductId());
            
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setOriginalPrice(product.getOriginalPrice());
            orderItem.setGroupPrice(product.getGroupPrice());
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setTotalAmount(product.getGroupPrice().multiply(new BigDecimal(itemRequest.getQuantity())));
            
            orderItemService.save(orderItem);
            
            // 减少库存
            productService.decreaseStock(product.getId(), itemRequest.getQuantity());
        }
        
        return order;
    }
    
    @Override
    public boolean payOrder(String orderNo, String paymentMethod) {
        Order order = getOrderByOrderNo(orderNo);
        if (order != null && "PENDING_PAYMENT".equals(order.getStatus())) {
            order.setStatus("PAID");
            order.setPaymentMethod(paymentMethod);
            order.setPaymentTime(LocalDateTime.now());
            return this.updateById(order);
        }
        return false;
    }
    
    @Override
    public boolean cancelOrder(String orderNo, Long userId) {
        Order order = getOrderByOrderNo(orderNo);
        if (order != null && order.getUserId().equals(userId) && 
            ("PENDING_PAYMENT".equals(order.getStatus()) || "PAID".equals(order.getStatus()))) {
            order.setStatus("CANCELLED");
            
            // 恢复库存
            List<OrderItem> orderItems = orderItemService.getByOrderId(order.getId());
            for (OrderItem item : orderItems) {
                productService.increaseStock(item.getProductId(), item.getQuantity());
            }
            
            return this.updateById(order);
        }
        return false;
    }
    
    @Override
    public boolean confirmDelivery(String orderNo, Long userId) {
        Order order = getOrderByOrderNo(orderNo);
        if (order != null && order.getUserId().equals(userId) && "SHIPPED".equals(order.getStatus())) {
            order.setStatus("DELIVERED");
            order.setDeliveryTime(LocalDateTime.now());
            
            // 增加销量
            List<OrderItem> orderItems = orderItemService.getByOrderId(order.getId());
            for (OrderItem item : orderItems) {
                productService.increaseSalesCount(item.getProductId(), item.getQuantity());
            }
            
            return this.updateById(order);
        }
        return false;
    }
    
    @Override
    public boolean shipOrder(String orderNo) {
        Order order = getOrderByOrderNo(orderNo);
        if (order != null && "PAID".equals(order.getStatus())) {
            order.setStatus("SHIPPED");
            order.setShipTime(LocalDateTime.now());
            return this.updateById(order);
        }
        return false;
    }
    
    @Override
    public Page<Order> getUserOrders(Long userId, String status, Integer current, Integer size) {
        Page<Order> page = new Page<>(current, size);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("created_at");
        
        return this.page(page, queryWrapper);
    }
    
    @Override
    public Order getOrderByOrderNo(String orderNo) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        return this.getOne(queryWrapper);
    }
    
    @Override
    public String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        long counter = orderCounter.getAndIncrement();
        return "GB" + timestamp + String.format("%04d", counter % 10000);
    }
    
    @Override
    public Page<Order> getGroupLeaderOrders(Long groupLeaderId, String status, Integer current, Integer size) {
        Page<Order> page = new Page<>(current, size);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_leader_id", groupLeaderId);
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        return this.page(page, queryWrapper);
    }
    
    @Override
    public Map<String, Object> getGroupLeaderOrderStatistics(Long groupLeaderId) {
        Map<String, Object> statistics = new HashMap<>();
        
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_leader_id", groupLeaderId);
        
        // 总订单数
        Long totalOrders = this.count(queryWrapper);
        statistics.put("totalOrders", totalOrders);
        
        // 各状态订单数
        QueryWrapper<Order> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("group_leader_id", groupLeaderId).eq("status", "PENDING");
        Long pendingOrders = this.count(pendingWrapper);
        statistics.put("pendingOrders", pendingOrders);
        
        QueryWrapper<Order> paidWrapper = new QueryWrapper<>();
        paidWrapper.eq("group_leader_id", groupLeaderId).eq("status", "PAID");
        Long paidOrders = this.count(paidWrapper);
        statistics.put("paidOrders", paidOrders);
        
        QueryWrapper<Order> shippedWrapper = new QueryWrapper<>();
        shippedWrapper.eq("group_leader_id", groupLeaderId).eq("status", "SHIPPED");
        Long shippedOrders = this.count(shippedWrapper);
        statistics.put("shippedOrders", shippedOrders);
        
        QueryWrapper<Order> completedWrapper = new QueryWrapper<>();
        completedWrapper.eq("group_leader_id", groupLeaderId).eq("status", "COMPLETED");
        Long completedOrders = this.count(completedWrapper);
        statistics.put("completedOrders", completedOrders);
        
        // 总销售额（已支付和已完成的订单）
        QueryWrapper<Order> salesWrapper = new QueryWrapper<>();
        salesWrapper.eq("group_leader_id", groupLeaderId)
                   .in("status", "PAID", "SHIPPED", "COMPLETED");
        List<Order> salesOrders = this.list(salesWrapper);
        Double totalSales = salesOrders.stream()
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();
        statistics.put("totalSales", totalSales);
        
        return statistics;
    }
    
    @Override
    public Page<Order> getSupplierOrders(Long supplierId, String status, Integer current, Integer size) {
        Page<Order> page = new Page<>(current, size);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        // 通过订单项关联查询供应商的订单
        queryWrapper.exists("SELECT 1 FROM order_items oi JOIN products p ON oi.product_id = p.id " +
                           "WHERE oi.order_id = orders.id AND p.supplier_id = {0}", supplierId);
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("created_at");
        
        Page<Order> result = this.page(page, queryWrapper);
        
        // 为每个订单加载订单项信息
        for (Order order : result.getRecords()) {
            List<OrderItem> orderItems = orderItemService.getByOrderId(order.getId());
            // 只保留属于该供应商的订单项
            List<OrderItem> supplierItems = orderItems.stream()
                .filter(item -> {
                    Product product = productService.getById(item.getProductId());
                    return product != null && supplierId.equals(product.getSupplierId());
                })
                .collect(Collectors.toList());
            order.setOrderItems(supplierItems);
        }
        
        return result;
    }
    
    @Override
    public Page<Order> getAllOrders(String status, String orderNo, Integer current, Integer size) {
        Page<Order> page = new Page<>(current, size);
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        if (orderNo != null && !orderNo.isEmpty()) {
            queryWrapper.like("order_no", orderNo);
        }
        queryWrapper.orderByDesc("create_time");
        return this.page(page, queryWrapper);
    }
    
    @Override
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总订单数
        Long totalOrders = this.count();
        statistics.put("totalOrders", totalOrders);
        
        // 各状态订单数
        QueryWrapper<Order> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("status", "PENDING");
        Long pendingOrders = this.count(pendingWrapper);
        statistics.put("pendingOrders", pendingOrders);
        
        QueryWrapper<Order> paidWrapper = new QueryWrapper<>();
        paidWrapper.eq("status", "PAID");
        Long paidOrders = this.count(paidWrapper);
        statistics.put("paidOrders", paidOrders);
        
        QueryWrapper<Order> shippedWrapper = new QueryWrapper<>();
        shippedWrapper.eq("status", "SHIPPED");
        Long shippedOrders = this.count(shippedWrapper);
        statistics.put("shippedOrders", shippedOrders);
        
        QueryWrapper<Order> completedWrapper = new QueryWrapper<>();
        completedWrapper.eq("status", "COMPLETED");
        Long completedOrders = this.count(completedWrapper);
        statistics.put("completedOrders", completedOrders);
        
        QueryWrapper<Order> cancelledWrapper = new QueryWrapper<>();
        cancelledWrapper.eq("status", "CANCELLED");
        Long cancelledOrders = this.count(cancelledWrapper);
        statistics.put("cancelledOrders", cancelledOrders);
        
        // 总销售额（已支付和已完成的订单）
        QueryWrapper<Order> salesWrapper = new QueryWrapper<>();
        salesWrapper.in("status", "PAID", "SHIPPED", "COMPLETED");
        List<Order> salesOrders = this.list(salesWrapper);
        Double totalSales = salesOrders.stream()
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();
        statistics.put("totalSales", totalSales);
        
        // 今日订单数
        QueryWrapper<Order> todayWrapper = new QueryWrapper<>();
        todayWrapper.ge("create_time", LocalDateTime.now().toLocalDate());
        Long todayOrders = this.count(todayWrapper);
        statistics.put("todayOrders", todayOrders);
        
        // 今日销售额
        QueryWrapper<Order> todaySalesWrapper = new QueryWrapper<>();
        todaySalesWrapper.ge("create_time", LocalDateTime.now().toLocalDate())
                        .in("status", "PAID", "SHIPPED", "COMPLETED");
        List<Order> todaySalesOrders = this.list(todaySalesWrapper);
        Double todaySales = todaySalesOrders.stream()
                .mapToDouble(order -> order.getTotalAmount().doubleValue())
                .sum();
        statistics.put("todaySales", todaySales);
        
        return statistics;
    }
    
    @Override
    public boolean applyRefund(String orderNo, Long userId, String reason) {
        Order order = getOrderByOrderNo(orderNo);
        if (order != null && order.getUserId().equals(userId) && 
            ("PAID".equals(order.getStatus()) || "SHIPPED".equals(order.getStatus()) || "DELIVERED".equals(order.getStatus()))) {
            order.setStatus("REFUND_PENDING");
            order.setRefundReason(reason);
            order.setRefundApplyTime(LocalDateTime.now());
            return this.updateById(order);
        }
        return false;
    }
    
    @Override
    public boolean processRefund(String orderNo, String action, String remark) {
        Order order = getOrderByOrderNo(orderNo);
        if (order != null && "REFUND_PENDING".equals(order.getStatus())) {
            if ("APPROVE".equals(action)) {
                order.setStatus("REFUNDED");
                order.setRefundTime(LocalDateTime.now());
                
                // 恢复库存
                List<OrderItem> orderItems = orderItemService.getByOrderId(order.getId());
                for (OrderItem item : orderItems) {
                    productService.increaseStock(item.getProductId(), item.getQuantity());
                }
            } else if ("REJECT".equals(action)) {
                order.setStatus("REFUND_REJECTED");
            }
            order.setRefundRemark(remark);
            order.setRefundProcessTime(LocalDateTime.now());
            return this.updateById(order);
        }
        return false;
    }
    
    @Override
    public boolean reviewOrder(String orderNo, Long userId, Integer rating, String comment) {
        Order order = getOrderByOrderNo(orderNo);
        if (order != null && order.getUserId().equals(userId) && "DELIVERED".equals(order.getStatus())) {
            order.setRating(rating);
            order.setReviewComment(comment);
            order.setReviewTime(LocalDateTime.now());
            return this.updateById(order);
        }
        return false;
    }
    
    @Override
    public Map<String, Object> getLogistics(String orderNo, Long userId) {
        Order order = getOrderByOrderNo(orderNo);
        Map<String, Object> logistics = new HashMap<>();
        
        if (order != null && order.getUserId().equals(userId)) {
            logistics.put("orderNo", order.getOrderNo());
            logistics.put("status", order.getStatus());
            logistics.put("logisticsCompany", order.getLogisticsCompany());
            logistics.put("trackingNumber", order.getTrackingNumber());
            logistics.put("shipTime", order.getShipTime());
            logistics.put("deliveryTime", order.getDeliveryTime());
            
            // 模拟物流轨迹
            List<Map<String, Object>> tracks = new java.util.ArrayList<>();
            if (order.getShipTime() != null) {
                Map<String, Object> track1 = new HashMap<>();
                track1.put("time", order.getShipTime());
                track1.put("status", "已发货");
                track1.put("description", "商品已从仓库发出");
                tracks.add(track1);
            }
            if (order.getDeliveryTime() != null) {
                Map<String, Object> track2 = new HashMap<>();
                track2.put("time", order.getDeliveryTime());
                track2.put("status", "已送达");
                track2.put("description", "商品已送达收货地址");
                tracks.add(track2);
            }
            logistics.put("tracks", tracks);
        }
        
        return logistics;
    }
    
    @Override
    public boolean updateLogistics(String orderNo, Long userId, String logisticsCompany, String trackingNumber) {
        Order order = getOrderByOrderNo(orderNo);
        if (order != null) {
            // 验证用户是否为该订单的供应商
            List<OrderItem> orderItems = orderItemService.getByOrderId(order.getId());
            boolean isSupplier = orderItems.stream().anyMatch(item -> {
                Product product = productService.getById(item.getProductId());
                return product != null && product.getSupplierId().equals(userId);
            });
            
            if (isSupplier && "PAID".equals(order.getStatus())) {
                order.setLogisticsCompany(logisticsCompany);
                order.setTrackingNumber(trackingNumber);
                order.setStatus("SHIPPED");
                order.setShipTime(LocalDateTime.now());
                return this.updateById(order);
            }
        }
        return false;
    }
    
    @Override
    public Map<String, Object> batchShip(List<String> orderNos, Long userId) {
        Map<String, Object> result = new HashMap<>();
        int successCount = 0;
        int failCount = 0;
        List<String> failedOrders = new java.util.ArrayList<>();
        
        for (String orderNo : orderNos) {
            Order order = getOrderByOrderNo(orderNo);
            if (order != null && "PAID".equals(order.getStatus())) {
                // 验证用户是否为该订单的供应商
                List<OrderItem> orderItems = orderItemService.getByOrderId(order.getId());
                boolean isSupplier = orderItems.stream().anyMatch(item -> {
                    Product product = productService.getById(item.getProductId());
                    return product != null && product.getSupplierId().equals(userId);
                });
                
                if (isSupplier) {
                    order.setStatus("SHIPPED");
                    order.setShipTime(LocalDateTime.now());
                    if (this.updateById(order)) {
                        successCount++;
                    } else {
                        failCount++;
                        failedOrders.add(orderNo);
                    }
                } else {
                    failCount++;
                    failedOrders.add(orderNo);
                }
            } else {
                failCount++;
                failedOrders.add(orderNo);
            }
        }
        
        result.put("successCount", successCount);
        result.put("failCount", failCount);
        result.put("failedOrders", failedOrders);
        
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getExportData(String startDate, String endDate, String status) {
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        
        if (startDate != null && !startDate.isEmpty()) {
            queryWrapper.ge("created_at", startDate);
        }
        if (endDate != null && !endDate.isEmpty()) {
            queryWrapper.le("created_at", endDate);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.eq("status", status);
        }
        
        List<Order> orders = this.list(queryWrapper);
        List<Map<String, Object>> exportData = new java.util.ArrayList<>();
        
        for (Order order : orders) {
            Map<String, Object> data = new HashMap<>();
            data.put("orderNo", order.getOrderNo());
            data.put("userId", order.getUserId());
            data.put("totalAmount", order.getTotalAmount());
            data.put("actualAmount", order.getActualAmount());
            data.put("status", order.getStatus());
            data.put("paymentMethod", order.getPaymentMethod());
            data.put("receiverName", order.getReceiverName());
            data.put("receiverPhone", order.getReceiverPhone());
            data.put("receiverAddress", order.getReceiverAddress());
            data.put("createdAt", order.getCreatedAt());
            data.put("paymentTime", order.getPaymentTime());
            data.put("shipTime", order.getShipTime());
            data.put("deliveryTime", order.getDeliveryTime());
            exportData.add(data);
        }
        
        return exportData;
    }
    
    @Override
    public boolean confirmReceipt(String orderNo, Long userId) {
        Order order = getOrderByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            return false;
        }
        
        if (!"DELIVERED".equals(order.getStatus())) {
            return false;
        }
        
        order.setStatus("COMPLETED");
        return updateById(order);
    }
    
    @Override
    public Order getOrderWithItems(Long orderId) {
        Order order = getById(orderId);
        if (order != null) {
            List<OrderItem> orderItems = orderItemService.getByOrderId(orderId);
            order.setOrderItems(orderItems);
        }
        return order;
    }
}