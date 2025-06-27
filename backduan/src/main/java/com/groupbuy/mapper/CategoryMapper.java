package com.groupbuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.groupbuy.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    
    /**
     * 获取所有激活状态的分类
     */
    @Select("SELECT * FROM categories WHERE is_active = true ORDER BY sort_order ASC")
    List<Category> findAllActiveCategories();
    
    /**
     * 根据排序获取分类
     */
    @Select("SELECT * FROM categories ORDER BY sort_order ASC, created_at DESC")
    List<Category> findAllOrderBySort();
}