package com.groupbuy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("orders")
public class Order {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("order_no")
    private String orderNo;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("group_leader_id")
    private Long groupLeaderId;
    
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    @TableField("discount_amount")
    private BigDecimal discountAmount;
    
    @TableField("actual_amount")
    private BigDecimal actualAmount;
    
    @TableField("commission_amount")
    private BigDecimal commissionAmount;
    
    private String status;
    
    @TableField("payment_method")
    private String paymentMethod;
    
    @TableField("payment_time")
    private LocalDateTime paymentTime;
    
    @TableField("ship_time")
    private LocalDateTime shipTime;
    
    @TableField("delivery_time")
    private LocalDateTime deliveryTime;
    
    @TableField("receiver_name")
    private String receiverName;
    
    @TableField("receiver_phone")
    private String receiverPhone;
    
    @TableField("receiver_address")
    private String receiverAddress;
    
    private String remark;
    
    // 退款相关字段
    @TableField("refund_reason")
    private String refundReason;
    
    @TableField("refund_remark")
    private String refundRemark;
    
    @TableField("refund_apply_time")
    private LocalDateTime refundApplyTime;
    
    @TableField("refund_process_time")
    private LocalDateTime refundProcessTime;
    
    @TableField("refund_time")
    private LocalDateTime refundTime;
    
    // 评价相关字段
    private Integer rating;
    
    @TableField("review_comment")
    private String reviewComment;
    
    @TableField("review_time")
    private LocalDateTime reviewTime;
    
    // 物流相关字段
    @TableField("logistics_company")
    private String logisticsCompany;
    
    @TableField("tracking_number")
    private String trackingNumber;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 非数据库字段
    @TableField(exist = false)
    private List<OrderItem> orderItems;
    
    @TableField(exist = false)
    private String userName;
    
    @TableField(exist = false)
    private String groupLeaderName;
}