package com.groupbuy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.groupbuy.entity.Product;

import java.util.List;
import java.util.Map;

public interface ProductService extends IService<Product> {
    
    /**
     * 分页查询商品
     */
    Page<Product> getProductPage(Integer current, Integer size, Long categoryId, String keyword, String status);
    
    /**
     * 获取热销商品
     */
    List<Product> getHotProducts(Integer limit);
    
    /**
     * 获取推荐商品
     */
    List<Product> getRecommendProducts(Integer limit);
    
    /**
     * 增加商品浏览量
     */
    void increaseViewCount(Long productId);
    
    /**
     * 增加商品销量
     */
    void increaseSalesCount(Long productId, Integer quantity);
    
    /**
     * 减少商品库存
     */
    boolean decreaseStock(Long productId, Integer quantity);
    
    /**
     * 增加商品库存
     */
    void increaseStock(Long productId, Integer quantity);
    
    /**
     * 根据供应商ID获取商品列表
     */
    List<Product> getProductsBySupplierId(Long supplierId);
    
    /**
     * 管理员获取商品列表（带分页、过滤、排序）
     */
    Page<Product> getAdminProductList(Integer current, Integer size, String status, 
                                     Long categoryId, Long supplierId, String keyword, 
                                     String sortBy, String sortOrder);
    
    /**
     * 供应商获取自己的商品列表
     */
    Page<Product> getSupplierProductList(Long supplierId, Integer current, Integer size, 
                                        String status, Long categoryId, String keyword, 
                                        String sortBy, String sortOrder);
    
    /**
     * 获取商品统计信息（管理员）
     */
    Map<String, Object> getProductStats();
    
    /**
     * 获取供应商商品统计信息
     */
    Map<String, Object> getSupplierProductStats(Long supplierId);
    
    /**
     * 批量更新商品状态
     */
    boolean batchUpdateStatus(List<Long> productIds, Integer status);
    
    /**
     * 批量删除商品
     */
    boolean batchDeleteProducts(List<Long> productIds);
    
    /**
     * 商品审核
     */
    boolean auditProduct(Long productId, Integer status, String rejectReason);
    
    /**
     * 获取待审核商品列表
     */
    Page<Product> getPendingAuditProducts(Integer current, Integer size);
    
    /**
     * 检查商品库存是否充足
     */
    boolean checkStock(Long productId, Integer quantity);
    
    /**
     * 获取低库存商品列表
     */
    List<Product> getLowStockProducts(Integer threshold);
    
    /**
     * 商品价格历史记录
     */
    List<Map<String, Object>> getPriceHistory(Long productId);
    
    /**
     * 更新商品价格
     */
    boolean updatePrice(Long productId, Double originalPrice, Double groupPrice);
}