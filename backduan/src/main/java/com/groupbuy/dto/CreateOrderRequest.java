package com.groupbuy.dto;

import lombok.Data;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CreateOrderRequest {
    
    @NotEmpty(message = "订单商品不能为空")
    @Valid
    private List<OrderItemRequest> items;
    
    private Long groupLeaderId;
    
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;
    
    @NotBlank(message = "收货人电话不能为空")
    private String receiverPhone;
    
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;
    
    private String remark;
    
    private BigDecimal discountAmount;
    
    @Data
    public static class OrderItemRequest {
        
        @NotNull(message = "商品ID不能为空")
        private Long productId;
        
        @NotNull(message = "商品数量不能为空")
        @Positive(message = "商品数量必须大于0")
        private Integer quantity;
    }
}