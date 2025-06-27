package com.groupbuy.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.groupbuy.entity.Category;
import java.util.List;
import java.util.Map;

public interface CategoryService extends IService<Category> {
    List<Category> getAllActiveCategories();
    Category addCategory(Category category);
    Category updateCategory(Category category);
    void deleteCategory(Long id);
    
    /**
     * 管理员获取分类列表（带分页、过滤、排序）
     */
    Page<Category> getAdminCategoryList(Integer current, Integer size, String keyword, 
                                       String status, String sortBy, String sortOrder);
    
    /**
     * 获取分类统计信息
     */
    Map<String, Object> getCategoryStats();
}