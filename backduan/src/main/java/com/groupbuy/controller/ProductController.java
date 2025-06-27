package com.groupbuy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.common.Result;
import com.groupbuy.entity.Product;
import com.groupbuy.service.ProductService;
import com.groupbuy.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8080")
public class ProductController {
    
    private final ProductService productService;
    private final JwtUtils jwtUtils;
    
    @GetMapping("/list")
    public Result<Page<Product>> getProductList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        
        Page<Product> page = new Page<>(current, size);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        
        // 只显示已上架的商品
        queryWrapper.eq("status", "ON_SALE");
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            queryWrapper.like("name", keyword)
                       .or()
                       .like("description", keyword);
        }
        
        queryWrapper.orderByDesc("created_at");
        
        Page<Product> result = productService.page(page, queryWrapper);
        return Result.success(result);
    }
    
    @GetMapping("/{id}")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Product product = productService.getById(id);
        if (product == null) {
            return Result.error("商品不存在");
        }
        
        // 增加浏览量
        product.setViewCount(product.getViewCount() + 1);
        productService.updateById(product);
        
        return Result.success(product);
    }
    
    @GetMapping("/hot")
    public Result<List<Product>> getHotProducts(@RequestParam(defaultValue = "10") Integer limit) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ON_SALE")
                   .orderByDesc("sales_count")
                   .last("LIMIT " + limit);
        
        List<Product> products = productService.list(queryWrapper);
        return Result.success(products);
    }
    
    @GetMapping("/recommend")
    public Result<List<Product>> getRecommendProducts(@RequestParam(defaultValue = "10") Integer limit) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ON_SALE")
                   .orderByDesc("view_count")
                   .last("LIMIT " + limit);
        
        List<Product> products = productService.list(queryWrapper);
        return Result.success(products);
    }
    
    // 管理员获取所有商品（带分页、过滤、排序）
    @GetMapping("/admin/list")
    public Result<Page<Product>> getAdminProductList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "created_at") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Page<Product> result = productService.getAdminProductList(
                current, size, status, categoryId, supplierId, keyword, sortBy, sortOrder);
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("获取商品列表失败: " + e.getMessage());
        }
    }
    
    // 供应商获取自己的商品列表
    @GetMapping("/supplier/list")
    public Result<Page<Product>> getSupplierProductList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "created_at") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"SUPPLIER".equals(role)) {
                return Result.error("权限不足");
            }
            
            Page<Product> result = productService.getSupplierProductList(
                userId, current, size, status, categoryId, keyword, sortBy, sortOrder);
            return Result.success(result);
            
        } catch (Exception e) {
            return Result.error("获取商品列表失败: " + e.getMessage());
        }
    }
    
    // 供应商添加商品
    @PostMapping("/supplier/add")
    public Result<String> addProduct(@Valid @RequestBody Product product,
                                    @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"SUPPLIER".equals(role)) {
                return Result.error("权限不足");
            }
            
            product.setSupplierId(userId);
            product.setStatus("PENDING"); // 待审核状态
            productService.save(product);
            
            return Result.success("商品添加成功，等待审核");
            
        } catch (Exception e) {
            return Result.error("添加商品失败: " + e.getMessage());
        }
    }
    
    // 供应商更新商品
    @PutMapping("/supplier/update/{id}")
    public Result<String> updateProduct(@PathVariable Long id,
                                       @Valid @RequestBody Product product,
                                       @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"SUPPLIER".equals(role)) {
                return Result.error("权限不足");
            }
            
            Product existingProduct = productService.getById(id);
            if (existingProduct == null) {
                return Result.error("商品不存在");
            }
            
            if (!existingProduct.getSupplierId().equals(userId)) {
                return Result.error("只能修改自己的商品");
            }
            
            product.setId(id);
            product.setSupplierId(userId);
            productService.updateById(product);
            
            return Result.success("商品更新成功");
            
        } catch (Exception e) {
            return Result.error("更新商品失败: " + e.getMessage());
        }
    }
    
    // 管理员审核商品
    @PutMapping("/admin/review/{id}")
    public Result<String> reviewProduct(@PathVariable Long id,
                                       @RequestParam String status,
                                       @RequestParam(required = false) String reason,
                                       @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Product product = productService.getById(id);
            if (product == null) {
                return Result.error("商品不存在");
            }
            
            product.setStatus(status);
            if (reason != null && !reason.trim().isEmpty()) {
                product.setRejectReason(reason);
            }
            productService.updateById(product);
            
            return Result.success("审核完成");
            
        } catch (Exception e) {
            return Result.error("审核失败: " + e.getMessage());
        }
    }
    
    // 获取商品统计信息
    @GetMapping("/admin/stats")
    public Result<Map<String, Object>> getProductStats(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Map<String, Object> stats = productService.getProductStats();
            return Result.success(stats);
            
        } catch (Exception e) {
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }
    
    // 供应商获取自己的商品统计
    @GetMapping("/supplier/stats")
    public Result<Map<String, Object>> getSupplierProductStats(@RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            if (!"SUPPLIER".equals(role)) {
                return Result.error("权限不足");
            }
            
            Map<String, Object> stats = productService.getSupplierProductStats(userId);
            return Result.success(stats);
            
        } catch (Exception e) {
            return Result.error("获取统计信息失败: " + e.getMessage());
        }
    }
    
    // 批量更新商品状态
    @PutMapping("/admin/batch-status")
    public Result<String> batchUpdateStatus(@RequestBody Map<String, Object> request,
                                           @RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            @SuppressWarnings("unchecked")
            List<Long> productIds = (List<Long>) request.get("productIds");
            Integer status = (Integer) request.get("status");
            
            boolean success = productService.batchUpdateStatus(productIds, status);
            return success ? Result.success("批量更新成功") : Result.error("批量更新失败");
            
        } catch (Exception e) {
            return Result.error("批量更新失败: " + e.getMessage());
        }
    }
    
    // 批量删除商品
    @DeleteMapping("/admin/batch-delete")
    public Result<String> batchDeleteProducts(@RequestBody List<Long> productIds,
                                             @RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            boolean success = productService.batchDeleteProducts(productIds);
            return success ? Result.success("批量删除成功") : Result.error("批量删除失败");
            
        } catch (Exception e) {
            return Result.error("批量删除失败: " + e.getMessage());
        }
    }
    
    // 商品审核（新版本）
    @PutMapping("/admin/audit/{id}")
    public Result<String> auditProduct(@PathVariable Long id,
                                      @RequestParam Integer status,
                                      @RequestParam(required = false) String rejectReason,
                                      @RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            boolean success = productService.auditProduct(id, status, rejectReason);
            return success ? Result.success("审核完成") : Result.error("审核失败");
            
        } catch (Exception e) {
            return Result.error("审核失败: " + e.getMessage());
        }
    }
    
    // 获取待审核商品列表
    @GetMapping("/admin/pending-audit")
    public Result<Page<Product>> getPendingAuditProducts(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            Page<Product> page = productService.getPendingAuditProducts(current, size);
            return Result.success(page);
            
        } catch (Exception e) {
            return Result.error("获取待审核商品失败: " + e.getMessage());
        }
    }
    
    // 检查商品库存
    @GetMapping("/check-stock/{id}")
    public Result<Boolean> checkStock(@PathVariable Long id, @RequestParam Integer quantity) {
        try {
            boolean hasStock = productService.checkStock(id, quantity);
            return Result.success(hasStock);
        } catch (Exception e) {
            return Result.error("检查库存失败: " + e.getMessage());
        }
    }
    
    // 获取低库存商品列表
    @GetMapping("/admin/low-stock")
    public Result<List<Product>> getLowStockProducts(
            @RequestParam(defaultValue = "10") Integer threshold,
            @RequestHeader("Authorization") String token) {
        try {
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            List<Product> products = productService.getLowStockProducts(threshold);
            return Result.success(products);
            
        } catch (Exception e) {
            return Result.error("获取低库存商品失败: " + e.getMessage());
        }
    }
    
    // 获取商品价格历史
    @GetMapping("/price-history/{id}")
    public Result<List<Map<String, Object>>> getPriceHistory(@PathVariable Long id) {
        try {
            List<Map<String, Object>> history = productService.getPriceHistory(id);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error("获取价格历史失败: " + e.getMessage());
        }
    }
    
    // 更新商品价格
    @PutMapping("/update-price/{id}")
    public Result<String> updatePrice(@PathVariable Long id,
                                     @RequestParam Double originalPrice,
                                     @RequestParam Double groupPrice,
                                     @RequestHeader("Authorization") String token) {
        try {
            Long userId = jwtUtils.getUserIdFromToken(token.replace("Bearer ", ""));
            String role = jwtUtils.getRoleFromToken(token.replace("Bearer ", ""));
            
            // 验证权限（供应商只能修改自己的商品，管理员可以修改所有商品）
            if ("SUPPLIER".equals(role)) {
                Product product = productService.getById(id);
                if (product == null || !product.getSupplierId().equals(userId)) {
                    return Result.error("权限不足");
                }
            } else if (!"ADMIN".equals(role)) {
                return Result.error("权限不足");
            }
            
            boolean success = productService.updatePrice(id, originalPrice, groupPrice);
            return success ? Result.success("价格更新成功") : Result.error("价格更新失败");
            
        } catch (Exception e) {
            return Result.error("价格更新失败: " + e.getMessage());
        }
    }
}