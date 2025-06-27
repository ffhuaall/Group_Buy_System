package com.groupbuy.service;

import com.groupbuy.dto.CartItemDTO;
import com.groupbuy.entity.ShoppingCart;
import java.util.List;

public interface ShoppingCartService {
    List<CartItemDTO> getUserCart(Long userId);
    ShoppingCart addToCart(Long userId, Long productId, Integer quantity);
    ShoppingCart updateCartItem(Long userId, Long productId, Integer quantity);
    void removeFromCart(Long userId, Long productId);
    void clearCart(Long userId);
    
    // 批量操作
    void batchRemoveFromCart(Long userId, List<Long> productIds);
    void batchUpdateCartItems(Long userId, List<CartItemDTO> items);
    
    // 购物车统计
    int getCartItemCount(Long userId);
    double getCartTotalAmount(Long userId);
    
    // 购物车验证
    boolean validateCartItems(Long userId);
    List<String> getInvalidCartItems(Long userId);
}