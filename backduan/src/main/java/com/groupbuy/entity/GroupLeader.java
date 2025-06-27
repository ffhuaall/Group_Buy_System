package com.groupbuy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("group_leaders")
public class GroupLeader {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("real_name")
    private String realName; // 真实姓名
    
    @TableField("phone")
    private String phone;
    
    @TableField("id_card")
    private String idCard; // 身份证号
    
    @TableField("city")
    private String city; // 城市
    
    @TableField("district")
    private String district; // 区域
    
    @TableField("address")
    private String address; // 详细地址
    
    @TableField("reason")
    private String reason; // 申请理由
    
    @TableField("community_name")
    private String communityName;
    
    @TableField("community_address")
    private String communityAddress;
    
    @TableField("description")
    private String description; // 描述信息
    
    @TableField("commission_rate")
    private BigDecimal commissionRate;
    
    @TableField(exist = false)
    private Long communityId; // 分配的社区ID（虚拟字段）
    
    @TableField("status")
    private String status; // PENDING, APPROVED, REJECTED, SUSPENDED
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    // 前端字段映射
    public void setName(String name) {
        this.realName = name;
    }
    
    public String getName() {
        return this.realName;
    }
}