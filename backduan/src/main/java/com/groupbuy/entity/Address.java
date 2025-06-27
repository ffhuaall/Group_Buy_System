package com.groupbuy.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Data
@TableName("addresses")
public class Address {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名长度不能超过50个字符")
    private String receiverName;
    
    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号码")
    private String receiverPhone;
    
    @NotBlank(message = "省份不能为空")
    @Size(max = 20, message = "省份名称长度不能超过20个字符")
    private String province;
    
    @NotBlank(message = "城市不能为空")
    @Size(max = 20, message = "城市名称长度不能超过20个字符")
    private String city;
    
    @NotBlank(message = "区县不能为空")
    @Size(max = 20, message = "区县名称长度不能超过20个字符")
    private String district;
    
    @NotBlank(message = "详细地址不能为空")
    @Size(max = 200, message = "详细地址长度不能超过200个字符")
    private String detailAddress;
    
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}