package com.groupbuy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("products")
public class Product {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("supplier_id")
    private Long supplierId;
    
    @TableField("category_id")
    private Long categoryId;
    
    private String name;
    
    private String description;
    
    @TableField("main_image")
    private String mainImage;
    
    private String images;
    
    @TableField("original_price")
    private BigDecimal originalPrice;
    
    @TableField("group_price")
    private BigDecimal groupPrice;
    
    @TableField("cost_price")
    private BigDecimal costPrice;
    
    private Integer stock;
    
    @TableField("min_group_size")
    private Integer minGroupSize;
    
    private String unit;
    
    private BigDecimal weight;
    
    @TableField("shelf_life")
    private Integer shelfLife;
    
    @TableField("storage_condition")
    private String storageCondition;
    
    private String status;
    
    @TableField("reject_reason")
    private String rejectReason; // 拒绝原因
    
    @TableField("sales_count")
    private Integer salesCount;
    
    @TableField("view_count")
    private Integer viewCount;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
    
    // 非数据库字段
    @TableField(exist = false)
    private String categoryName;
    
    @TableField(exist = false)
    private String supplierName;
}