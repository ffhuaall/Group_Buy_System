package com.groupbuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    
    /**
     * 获取商品详情（包含分类和供应商信息）
     */
    @Select("SELECT p.*, c.name as category_name, s.company_name as supplier_name " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
            "WHERE p.id = #{id}")
    Product getProductDetail(@Param("id") Long id);
    
    /**
     * 分页查询商品（包含分类和供应商信息）
     */
    @Select("SELECT p.*, c.name as category_name, s.company_name as supplier_name " +
            "FROM products p " +
            "LEFT JOIN categories c ON p.category_id = c.id " +
            "LEFT JOIN suppliers s ON p.supplier_id = s.id " +
            "WHERE p.status = #{status} " +
            "ORDER BY p.created_at DESC")
    Page<Product> getProductPageWithDetails(Page<Product> page, @Param("status") String status);
    
    /**
     * 更新商品库存
     */
    @Update("UPDATE products SET stock = stock - #{quantity} WHERE id = #{productId} AND stock >= #{quantity}")
    int decreaseStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 增加商品销量
     */
    @Update("UPDATE products SET sales_count = sales_count + #{quantity} WHERE id = #{productId}")
    int increaseSalesCount(@Param("productId") Long productId, @Param("quantity") Integer quantity);
    
    /**
     * 增加商品浏览量
     */
    @Update("UPDATE products SET view_count = view_count + 1 WHERE id = #{productId}")
    int increaseViewCount(@Param("productId") Long productId);
}