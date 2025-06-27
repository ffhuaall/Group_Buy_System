package com.groupbuy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("categories")
public class Category {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String name;  // 对应数据库的 name 字段
    
    @TableField("parent_id")
    private Long parentId;
    
    private Integer level;
    
    @TableField("sort_order")
    private Integer sortOrder;
    
    private String icon;
    
    private Integer status;  // 对应数据库的 status 字段（0-禁用，1-启用）
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}