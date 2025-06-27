package com.groupbuy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.groupbuy.entity.Category;
import com.groupbuy.mapper.CategoryMapper;
import com.groupbuy.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
    @Override
    public List<Category> getAllActiveCategories() {
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1)  // 修改为 status = 1（启用状态）
                   .orderByAsc("sort_order")
                   .orderByDesc("created_at");
        return list(queryWrapper);
    }
    
    @Override
    public Category addCategory(Category category) {
        save(category);
        return category;
    }
    
    @Override
    public Category updateCategory(Category category) {
        updateById(category);
        return category;
    }
    
    @Override
    public void deleteCategory(Long id) {
        removeById(id);
    }
    
    @Override
    public Page<Category> getAdminCategoryList(Integer current, Integer size, String keyword, 
                                             String status, String sortBy, String sortOrder) {
        Page<Category> page = new Page<>(current, size);
        QueryWrapper<Category> queryWrapper = new QueryWrapper<>();
        
        if (StringUtils.hasText(status)) {
            queryWrapper.eq("status", status);
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
    public Map<String, Object> getCategoryStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 总分类数
        long totalCategories = this.count();
        stats.put("totalCategories", totalCategories);
        
        // 启用的分类数
        QueryWrapper<Category> activeWrapper = new QueryWrapper<>();
        activeWrapper.eq("status", "ACTIVE");
        long activeCount = this.count(activeWrapper);
        stats.put("activeCount", activeCount);
        
        // 禁用的分类数
        QueryWrapper<Category> inactiveWrapper = new QueryWrapper<>();
        inactiveWrapper.eq("status", "INACTIVE");
        long inactiveCount = this.count(inactiveWrapper);
        stats.put("inactiveCount", inactiveCount);
        
        // 今日新增分类数
        LocalDateTime todayStart = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        QueryWrapper<Category> todayWrapper = new QueryWrapper<>();
        todayWrapper.ge("created_at", todayStart);
        long todayNewCount = this.count(todayWrapper);
        stats.put("todayNewCount", todayNewCount);
        
        // 本月新增分类数
        LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        QueryWrapper<Category> monthWrapper = new QueryWrapper<>();
        monthWrapper.ge("created_at", monthStart);
        long monthNewCount = this.count(monthWrapper);
        stats.put("monthNewCount", monthNewCount);
        
        return stats;
    }
}