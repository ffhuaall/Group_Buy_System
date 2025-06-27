package com.groupbuy.dto;

import lombok.Data;

@Data
public class ProductQueryRequest {
    
    private Long categoryId;
    
    private String keyword;
    
    private String status;
    
    private Long supplierId;
    
    private Integer current = 1;
    
    private Integer size = 10;
    
    private String sortField = "created_at";
    
    private String sortOrder = "desc";
}