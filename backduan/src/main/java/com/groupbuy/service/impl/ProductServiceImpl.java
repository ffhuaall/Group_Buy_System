package com.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.groupbuy.entity.Product;
import com.groupbuy.mapper.ProductMapper;
import com.groupbuy.service.ProductService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {
    
    @Override
    public Page<Product> getProductPage(Integer current, Integer size, Long categoryId, String keyword, String status) {
        Page<Product> page = new Page<>(current, size);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("name", keyword)
                .or()
                .like("description", keyword)
            );
        }
        
        queryWrapper.orderByDesc("created_at");
        
        return this.page(page, queryWrapper);
    }
    
    @Override
    public List<Product> getHotProducts(Integer limit) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ON_SALE")
                   .orderByDesc("sales_count")
                   .last("LIMIT " + limit);
        return this.list(queryWrapper);
    }
    
    @Override
    public List<Product> getRecommendProducts(Integer limit) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "ON_SALE")
                   .orderByDesc("view_count")
                   .last("LIMIT " + limit);
        return this.list(queryWrapper);
    }
    
    @Override
    public void increaseViewCount(Long productId) {
        Product product = this.getById(productId);
        if (product != null) {
            product.setViewCount(product.getViewCount() + 1);
            this.updateById(product);
        }
    }
    
    @Override
    public void increaseSalesCount(Long productId, Integer quantity) {
        Product product = this.getById(productId);
        if (product != null) {
            product.setSalesCount(product.getSalesCount() + quantity);
            this.updateById(product);
        }
    }
    
    @Override
    public boolean decreaseStock(Long productId, Integer quantity) {
        Product product = this.getById(productId);
        if (product != null && product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            return this.updateById(product);
        }
        return false;
    }
    
    @Override
    public void increaseStock(Long productId, Integer quantity) {
        Product product = this.getById(productId);
        if (product != null) {
            product.setStock(product.getStock() + quantity);
            this.updateById(product);
        }
    }
    
    @Override
    public List<Product> getProductsBySupplierId(Long supplierId) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("supplier_id", supplierId)
                   .orderByDesc("created_at");
        return this.list(queryWrapper);
    }
    
    @Override
    public Page<Product> getAdminProductList(Integer current, Integer size, String status, 
                                           Long categoryId, Long supplierId, String keyword, 
                                           String sortBy, String sortOrder) {
        Page<Product> page = new Page<>(current, size);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        if (supplierId != null) {
            queryWrapper.eq("supplier_id", supplierId);
        }
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("name", keyword)
                .or()
                .like("description", keyword)
            );
        }
        
        // 排序
        if ("desc".equalsIgnoreCase(sortOrder)) {
            queryWrapper.orderByDesc(sortBy);
        } else {
            queryWrapper.orderByAsc(sortBy);
        }
        
        return this.page(page, queryWrapper);
    }
    
    @Override
    public Page<Product> getSupplierProductList(Long supplierId, Integer current, Integer size, 
                                              String status, Long categoryId, String keyword, 
                                              String sortBy, String sortOrder) {
        Page<Product> page = new Page<>(current, size);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        
        queryWrapper.eq("supplier_id", supplierId);
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
        }
        
        if (categoryId != null) {
            queryWrapper.eq("category_id", categoryId);
        }
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like("name", keyword)
                .or()
                .like("description", keyword)
            );
        }
        
        // 排序
        if ("desc".equalsIgnoreCase(sortOrder)) {
            queryWrapper.orderByDesc(sortBy);
        } else {
            queryWrapper.orderByAsc(sortBy);
        }
        
        return this.page(page, queryWrapper);
    }
    
    @Override
    public Map<String, Object> getProductStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总商品数
        long totalProducts = this.count();
        stats.put("totalProducts", totalProducts);
        
        // 各状态商品数
        QueryWrapper<Product> onSaleWrapper = new QueryWrapper<>();
        onSaleWrapper.eq("status", "ON_SALE");
        long onSaleCount = this.count(onSaleWrapper);
        stats.put("onSaleCount", onSaleCount);
        
        QueryWrapper<Product> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("status", "PENDING");
        long pendingCount = this.count(pendingWrapper);
        stats.put("pendingCount", pendingCount);
        
        QueryWrapper<Product> rejectedWrapper = new QueryWrapper<>();
        rejectedWrapper.eq("status", "REJECTED");
        long rejectedCount = this.count(rejectedWrapper);
        stats.put("rejectedCount", rejectedCount);
        
        QueryWrapper<Product> offSaleWrapper = new QueryWrapper<>();
        offSaleWrapper.eq("status", "OFF_SALE");
        long offSaleCount = this.count(offSaleWrapper);
        stats.put("offSaleCount", offSaleCount);
        
        // 今日新增商品数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        QueryWrapper<Product> todayWrapper = new QueryWrapper<>();
        todayWrapper.ge("created_at", todayStart);
        long todayNewCount = this.count(todayWrapper);
        stats.put("todayNewCount", todayNewCount);
        
        // 本月新增商品数
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        QueryWrapper<Product> monthWrapper = new QueryWrapper<>();
        monthWrapper.ge("created_at", monthStart);
        long monthNewCount = this.count(monthWrapper);
        stats.put("monthNewCount", monthNewCount);
        
        // 库存不足商品数（库存小于10）
        QueryWrapper<Product> lowStockWrapper = new QueryWrapper<>();
        lowStockWrapper.lt("stock", 10).eq("status", "ON_SALE");
        long lowStockCount = this.count(lowStockWrapper);
        stats.put("lowStockCount", lowStockCount);
        
        return stats;
    }
    
    @Override
    public Map<String, Object> getSupplierProductStats(Long supplierId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 总商品数
        QueryWrapper<Product> totalWrapper = new QueryWrapper<>();
        totalWrapper.eq("supplier_id", supplierId);
        long totalProducts = this.count(totalWrapper);
        stats.put("totalProducts", totalProducts);
        
        // 各状态商品数
        QueryWrapper<Product> onSaleWrapper = new QueryWrapper<>();
        onSaleWrapper.eq("supplier_id", supplierId).eq("status", "ON_SALE");
        long onSaleCount = this.count(onSaleWrapper);
        stats.put("onSaleCount", onSaleCount);
        
        QueryWrapper<Product> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("supplier_id", supplierId).eq("status", "PENDING");
        long pendingCount = this.count(pendingWrapper);
        stats.put("pendingCount", pendingCount);
        
        QueryWrapper<Product> rejectedWrapper = new QueryWrapper<>();
        rejectedWrapper.eq("supplier_id", supplierId).eq("status", "REJECTED");
        long rejectedCount = this.count(rejectedWrapper);
        stats.put("rejectedCount", rejectedCount);
        
        QueryWrapper<Product> offSaleWrapper = new QueryWrapper<>();
        offSaleWrapper.eq("supplier_id", supplierId).eq("status", "OFF_SALE");
        long offSaleCount = this.count(offSaleWrapper);
        stats.put("offSaleCount", offSaleCount);
        
        // 总销量
        List<Product> products = this.list(new QueryWrapper<Product>().eq("supplier_id", supplierId));
        int totalSales = products.stream().mapToInt(Product::getSalesCount).sum();
        stats.put("totalSales", totalSales);
        
        // 总浏览量
        int totalViews = products.stream().mapToInt(Product::getViewCount).sum();
        stats.put("totalViews", totalViews);
        
        // 库存不足商品数（库存小于10）
        QueryWrapper<Product> lowStockWrapper = new QueryWrapper<>();
        lowStockWrapper.eq("supplier_id", supplierId).lt("stock", 10).eq("status", "ON_SALE");
        long lowStockCount = this.count(lowStockWrapper);
        stats.put("lowStockCount", lowStockCount);
        
        return stats;
    }
    
    @Override
    public boolean updatePrice(Long productId, Double originalPrice, Double groupPrice) {
        Product product = this.getById(productId);
        if (product != null) {
            if (originalPrice != null) {
                product.setOriginalPrice(new java.math.BigDecimal(originalPrice.toString()));
            }
            if (groupPrice != null) {
                product.setGroupPrice(new java.math.BigDecimal(groupPrice.toString()));
            }
            return this.updateById(product);
        }
        return false;
    }
    
    @Override
    public List<Map<String, Object>> getPriceHistory(Long productId) {
        // 这里应该从价格历史表中查询数据，目前返回空列表
        // 如果有价格历史表，可以使用类似以下的查询逻辑：
        // return priceHistoryMapper.selectPriceHistoryByProductId(productId);
        
        
        Product product = this.getById(productId);
        List<Map<String, Object>> history = new java.util.ArrayList<>();
        if (product != null) {
            Map<String, Object> currentPrice = new HashMap<>();
            currentPrice.put("productId", productId);
            currentPrice.put("originalPrice", product.getOriginalPrice());
            currentPrice.put("groupPrice", product.getGroupPrice());
            currentPrice.put("changeTime", product.getUpdatedAt());
            currentPrice.put("changeType", "CURRENT");
            history.add(currentPrice);
        }
        return history;
    }
    
    @Override
    public boolean checkStock(Long productId, Integer quantity) {
        Product product = this.getById(productId);
        return product != null && product.getStock() >= quantity;
    }
    
    @Override
    public Page<Product> getPendingAuditProducts(Integer current, Integer size) {
        Page<Product> page = new Page<>(current, size);
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "PENDING_AUDIT");
        queryWrapper.orderByDesc("created_at");
        return this.page(page, queryWrapper);
    }
    
    @Override
    public boolean auditProduct(Long productId, Integer status, String rejectReason) {
        Product product = this.getById(productId);
        if (product == null) {
            return false;
        }
        
        if (status == 1) {
            product.setStatus("ON_SALE");
        } else {
            product.setStatus("REJECTED");
        }
        
        product.setUpdatedAt(LocalDateTime.now());
        return this.updateById(product);
    }
    
    @Override
    public boolean batchUpdateStatus(List<Long> productIds, Integer status) {
        if (productIds == null || productIds.isEmpty()) {
            return false;
        }
        
        String statusStr;
        switch (status) {
            case 1:
                statusStr = "ON_SALE";
                break;
            case 0:
                statusStr = "OFF_SALE";
                break;
            default:
                return false;
        }
        
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("id", productIds);
        
        Product updateProduct = new Product();
        updateProduct.setStatus(statusStr);
        updateProduct.setUpdatedAt(LocalDateTime.now());
        
        return this.update(updateProduct, queryWrapper);
    }
    
    @Override
    public List<Product> getLowStockProducts(Integer threshold) {
        QueryWrapper<Product> queryWrapper = new QueryWrapper<>();
        queryWrapper.le("stock", threshold != null ? threshold : 10)
                   .eq("status", "ON_SALE")
                   .orderByAsc("stock");
        return this.list(queryWrapper);
    }
    
    @Override
    public boolean batchDeleteProducts(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return false;
        }
        return this.removeByIds(productIds);
    }
}