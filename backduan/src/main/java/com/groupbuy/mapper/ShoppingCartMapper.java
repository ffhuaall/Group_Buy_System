package com.groupbuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.groupbuy.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Map;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
    
    @Select("SELECT sc.id, sc.user_id, sc.product_id, sc.quantity, sc.created_at, sc.updated_at, " +
            "p.name as product_name, p.description as product_description, " +
            "p.group_price as product_group_price, p.original_price as product_original_price, " +
            "p.stock as product_stock, p.main_image as product_image_url " +
            "FROM shopping_cart sc " +
            "LEFT JOIN products p ON sc.product_id = p.id " +
            "WHERE sc.user_id = #{userId} " +
            "ORDER BY sc.created_at DESC")
    List<Map<String, Object>> getUserCartWithProducts(@Param("userId") Long userId);
    
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} AND product_id = #{productId}")
    ShoppingCart findByUserIdAndProductId(@Param("userId") Long userId, @Param("productId") Long productId);
}