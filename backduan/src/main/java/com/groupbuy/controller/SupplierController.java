package com.groupbuy.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.common.Result;
import com.groupbuy.entity.Supplier;
import com.groupbuy.service.SupplierService;
import com.groupbuy.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/supplier")
public class SupplierController {
    
    @Autowired
    private SupplierService supplierService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @PostMapping("/add")
    public Result<Supplier> addSupplier(@Valid @RequestBody Supplier supplier) {
        Supplier savedSupplier = supplierService.addSupplier(supplier);
        return Result.success(savedSupplier);
    }
    
    @GetMapping("/list")
    public Result<Page<Supplier>> getAllSuppliers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword) {
        Page<Supplier> suppliers = supplierService.getSuppliersPage(page, size, status, keyword);
        return Result.success(suppliers);
    }
    
    @PutMapping("/update")
    public Result<Supplier> updateSupplier(@Valid @RequestBody Supplier supplier) {
        Supplier updatedSupplier = supplierService.updateSupplier(supplier);
        return Result.success(updatedSupplier);
    }
    
    @PutMapping("/status/{id}")
    public Result<Void> updateSupplierStatus(@PathVariable Long id, @RequestParam String status) {
        supplierService.updateSupplierStatus(id, status);
        return Result.success();
    }
    
    @GetMapping("/user/{userId}")
    public Result<Supplier> getSupplierByUserId(@PathVariable Long userId) {
        Supplier supplier = supplierService.getByUserId(userId);
        return Result.success(supplier);
    }
    
    @GetMapping("/info")
    public Result<Supplier> getCurrentSupplierInfo(@RequestHeader("Authorization") String token) {
        try {
            System.out.println("=== 获取供应商信息开始 ===");
            System.out.println("接收到的token: " + token);
            
            String actualToken = token.replace("Bearer ", "");
            System.out.println("处理后的token: " + actualToken);
            
            Long userId = jwtUtils.getUserIdFromToken(actualToken);
            String role = jwtUtils.getRoleFromToken(actualToken);
            
            System.out.println("从token解析的userId: " + userId);
            System.out.println("从token解析的role: " + role);
            
            if (!"SUPPLIER".equals(role)) {
                System.out.println("权限验证失败，当前角色: " + role);
                return Result.error("权限不足");
            }
            
            System.out.println("权限验证通过，开始查询供应商信息");
            Supplier supplier = supplierService.getByUserId(userId);
            System.out.println("查询到的供应商信息: " + (supplier != null ? supplier.getSupplierName() : "null"));
            
            if (supplier == null) {
                System.out.println("供应商信息不存在");
                return Result.error("供应商信息不存在");
            }
            
            System.out.println("=== 获取供应商信息成功 ===");
            return Result.success(supplier);
        } catch (Exception e) {
            System.out.println("获取供应商信息异常: " + e.getMessage());
            e.printStackTrace();
            return Result.error("获取供应商信息失败: " + e.getMessage());
        }
    }
}