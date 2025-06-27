package com.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.groupbuy.entity.Supplier;
import com.groupbuy.mapper.SupplierMapper;
import com.groupbuy.service.SupplierService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupplierServiceImpl extends ServiceImpl<SupplierMapper, Supplier> implements SupplierService {
    
    @Override
    public Supplier addSupplier(Supplier supplier) {
        supplier.setCreatedAt(LocalDateTime.now());
        supplier.setUpdatedAt(LocalDateTime.now());
        if (!StringUtils.hasText(supplier.getStatus())) {
            supplier.setStatus("ACTIVE");
        }
        this.save(supplier);
        return supplier;
    }
    
    @Override
    public List<Supplier> getSuppliers(int page, int size, String status) {
        Page<Supplier> pageObj = new Page<>(page, size);
        QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        
        queryWrapper.orderByDesc("created_at");
        
        Page<Supplier> result = this.page(pageObj, queryWrapper);
        return result.getRecords();
    }
    
    @Override
    public Page<Supplier> getSuppliersPage(int page, int size, String status, String keyword) {
        Page<Supplier> pageObj = new Page<>(page, size);
        QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("company_name", keyword)
                .or().like("contact_person", keyword)
                .or().like("contact_phone", keyword)
            );
        }
        
        queryWrapper.orderByDesc("created_at");
        
        return this.page(pageObj, queryWrapper);
    }
    
    @Override
    public Supplier updateSupplier(Supplier supplier) {
        supplier.setUpdatedAt(LocalDateTime.now());
        this.updateById(supplier);
        return supplier;
    }
    
    @Override
    public void updateSupplierStatus(Long id, String status) {
        Supplier supplier = this.getById(id);
        if (supplier != null) {
            supplier.setStatus(status);
            supplier.setUpdatedAt(LocalDateTime.now());
            this.updateById(supplier);
        }
    }
    
    @Override
    public Supplier getByUserId(Long userId) {
        QueryWrapper<Supplier> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        return this.getOne(queryWrapper);
    }
}