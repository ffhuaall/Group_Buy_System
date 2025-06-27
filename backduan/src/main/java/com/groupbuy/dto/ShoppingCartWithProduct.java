package com.groupbuy.dto;

import com.groupbuy.entity.Product;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShoppingCartWithProduct {
    private Long id;
    private Long userId;
    private Long productId;
    private Integer quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Product product;
}