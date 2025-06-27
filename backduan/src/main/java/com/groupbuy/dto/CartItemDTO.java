package com.groupbuy.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CartItemDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 商品信息
    private ProductInfo product;
    
    @Data
    public static class ProductInfo {
        private Long id;
        private String name;
        private String description;
        private Double groupPrice;
        private Double originalPrice;
        private Integer stock;
        private String imageUrl;
    }
}