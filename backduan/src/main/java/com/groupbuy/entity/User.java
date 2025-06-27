package com.groupbuy.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("users")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    private String username;
    
    private String password;
    
    private String phone;
    
    private String email;
    
    @TableField("real_name")
    private String realName;
    
    private String avatar;
    
    private Integer gender;
    
    private LocalDate birthday;
    
    private Integer status;
    
    @TableField("role")
    private String role;
    
    @TableField(value = "created_at", fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
    
    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}