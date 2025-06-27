package com.groupbuy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("suppliers")
public class Supplier {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("company_name")
    private String supplierName;
    
    @TableField("contact_person")
    private String contactPerson;
    
    @TableField("contact_phone")
    private String phone;
    
    @TableField("company_address")
    private String address;
    
    @TableField("business_license")
    private String businessLicense;
    
    private String status;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    // 新增数据库中存在的字段
    @TableField("user_id")
    private Long userId;
    
    private String description;
    
    @TableField("apply_time")
    private LocalDateTime applyTime;
    
    @TableField("approve_time")
    private LocalDateTime approveTime;
}