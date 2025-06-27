package com.groupbuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
    
    /**
     * 分页查询供应商
     */
    @Select("SELECT * FROM suppliers WHERE status = #{status} ORDER BY created_at DESC")
    Page<Supplier> findByStatusWithPage(Page<Supplier> page, @Param("status") String status);
    
    /**
     * 查询所有供应商
     */
    @Select("SELECT * FROM suppliers ORDER BY created_at DESC")
    Page<Supplier> findAllWithPage(Page<Supplier> page);
}