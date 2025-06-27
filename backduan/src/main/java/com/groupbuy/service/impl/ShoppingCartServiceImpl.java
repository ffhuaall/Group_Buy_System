package com.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.groupbuy.dto.CartItemDTO;
import com.groupbuy.entity.ShoppingCart;
import com.groupbuy.entity.Product;
import com.groupbuy.mapper.ShoppingCartMapper;
import com.groupbuy.service.ShoppingCartService;
import com.groupbuy.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
    
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    
    @Autowired
    private ProductService productService;
    
    @Override
    public List<CartItemDTO> getUserCart(Long userId) {
        List<Map<String, Object>> cartData = shoppingCartMapper.getUserCartWithProducts(userId);
        List<CartItemDTO> cartItems = new ArrayList<>();
        
        for (Map<String, Object> row : cartData) {
            CartItemDTO item = new CartItemDTO();
            item.setId(((Number) row.get("id")).longValue());
            item.setUserId(((Number) row.get("user_id")).longValue());
            item.setProductId(((Number) row.get("product_id")).longValue());
            item.setQuantity(((Number) row.get("quantity")).intValue());
            // 处理时间字段的类型转换
            java.sql.Timestamp createdAtTimestamp = (java.sql.Timestamp) row.get("created_at");
            java.sql.Timestamp updatedAtTimestamp = (java.sql.Timestamp) row.get("updated_at");
            item.setCreatedAt(createdAtTimestamp.toLocalDateTime());
            item.setUpdatedAt(updatedAtTimestamp.toLocalDateTime());
            
            // 设置商品信息
            CartItemDTO.ProductInfo product = new CartItemDTO.ProductInfo();
            product.setId(((Number) row.get("product_id")).longValue());
            product.setName((String) row.get("product_name"));
            product.setDescription((String) row.get("product_description"));
            if (row.get("product_group_price") != null) {
                product.setGroupPrice(((Number) row.get("product_group_price")).doubleValue());
            }
            if (row.get("product_original_price") != null) {
                product.setOriginalPrice(((Number) row.get("product_original_price")).doubleValue());
            }
            if (row.get("product_stock") != null) {
                product.setStock(((Number) row.get("product_stock")).intValue());
            }
            product.setImageUrl((String) row.get("product_image_url"));
            
            item.setProduct(product);
            cartItems.add(item);
        }
        
        return cartItems;
    }
    
    @Override
    public ShoppingCart addToCart(Long userId, Long productId, Integer quantity) {
        // 检查是否已存在该商品
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId);
        
        ShoppingCart existingItem = this.getOne(queryWrapper);
        
        if (existingItem != null) {
            // 如果已存在，更新数量
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            existingItem.setUpdatedAt(LocalDateTime.now());
            this.updateById(existingItem);
            return existingItem;
        } else {
            // 如果不存在，创建新的购物车项
            ShoppingCart cartItem = new ShoppingCart();
            cartItem.setUserId(userId);
            cartItem.setProductId(productId);
            cartItem.setQuantity(quantity);
            cartItem.setCreatedAt(LocalDateTime.now());
            cartItem.setUpdatedAt(LocalDateTime.now());
            this.save(cartItem);
            return cartItem;
        }
    }
    
    @Override
    public ShoppingCart updateCartItem(Long userId, Long productId, Integer quantity) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId);
        
        ShoppingCart cartItem = this.getOne(queryWrapper);
        if (cartItem != null) {
            cartItem.setQuantity(quantity);
            cartItem.setUpdatedAt(LocalDateTime.now());
            this.updateById(cartItem);
        }
        return cartItem;
    }
    
    @Override
    public void removeFromCart(Long userId, Long productId) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("product_id", productId);
        this.remove(queryWrapper);
    }
    
    @Override
    public void clearCart(Long userId) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        this.remove(queryWrapper);
    }
    
    @Override
    public void batchRemoveFromCart(Long userId, List<Long> productIds) {
        if (productIds != null && !productIds.isEmpty()) {
            QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId)
                       .in("product_id", productIds);
            this.remove(queryWrapper);
        }
    }
    
    @Override
    public void batchUpdateCartItems(Long userId, List<CartItemDTO> items) {
        if (items != null && !items.isEmpty()) {
            for (CartItemDTO item : items) {
                updateCartItem(userId, item.getProductId(), item.getQuantity());
            }
        }
    }
    
    @Override
    public int getCartItemCount(Long userId) {
        QueryWrapper<ShoppingCart> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<ShoppingCart> cartItems = this.list(queryWrapper);
        return cartItems.stream().mapToInt(ShoppingCart::getQuantity).sum();
    }
    
    @Override
    public double getCartTotalAmount(Long userId) {
        List<CartItemDTO> cartItems = getUserCart(userId);
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getGroupPrice() * item.getQuantity())
                .sum();
    }
    
    @Override
    public boolean validateCartItems(Long userId) {
        List<String> invalidItems = getInvalidCartItems(userId);
        return invalidItems.isEmpty();
    }
    
    @Override
    public List<String> getInvalidCartItems(Long userId) {
        List<CartItemDTO> cartItems = getUserCart(userId);
        List<String> invalidItems = new ArrayList<>();
        
        for (CartItemDTO item : cartItems) {
            Product product = productService.getById(item.getProductId());
            if (product == null) {
                invalidItems.add("商品不存在: " + item.getProduct().getName());
            } else if (!"ON_SALE".equals(product.getStatus())) {
                invalidItems.add("商品已下架: " + product.getName());
            } else if (product.getStock() < item.getQuantity()) {
                invalidItems.add("库存不足: " + product.getName() + "(库存:" + product.getStock() + ", 需要:" + item.getQuantity() + ")");
            }
        }
        
        return invalidItems;
    }
}