package com.groupbuy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_items")
public class OrderItem {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("order_id")
    private Long orderId;
    
    @TableField("product_id")
    private Long productId;
    
    @TableField("product_name")
    private String productName;
    
    @TableField("product_image")
    private String productImage;
    
    @TableField("original_price")
    private BigDecimal originalPrice;
    
    @TableField("group_price")
    private BigDecimal groupPrice;
    
    private Integer quantity;
    
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}