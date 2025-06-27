package com.groupbuy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.groupbuy.entity.OrderItem;

import java.util.List;

public interface OrderItemService extends IService<OrderItem> {
    
    /**
     * 根据订单ID获取订单详情
     */
    List<OrderItem> getByOrderId(Long orderId);
    
    /**
     * 根据商品ID获取订单详情
     */
    List<OrderItem> getByProductId(Long productId);
}