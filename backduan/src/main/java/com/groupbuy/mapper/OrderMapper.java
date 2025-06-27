package com.groupbuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.groupbuy.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    
    /**
     * 获取订单详情（包含用户和团长信息）
     */
    @Select("SELECT o.*, u.username as user_name, gl.community_name as group_leader_name " +
            "FROM orders o " +
            "LEFT JOIN users u ON o.user_id = u.id " +
            "LEFT JOIN group_leaders gl ON o.group_leader_id = gl.id " +
            "WHERE o.id = #{id}")
    Order getOrderDetail(@Param("id") Long id);
    
    /**
     * 分页查询订单（包含用户信息）
     */
    @Select("SELECT o.*, u.username as user_name " +
            "FROM orders o " +
            "LEFT JOIN users u ON o.user_id = u.id " +
            "ORDER BY o.created_at DESC")
    Page<Order> getOrderPageWithUserInfo(Page<Order> page);
    
    /**
     * 统计订单金额
     */
    @Select("SELECT SUM(actual_amount) FROM orders WHERE status IN ('PAID', 'SHIPPED', 'DELIVERED', 'COMPLETED')")
    BigDecimal getTotalOrderAmount();
    
    /**
     * 统计订单数量
     */
    @Select("SELECT COUNT(*) FROM orders WHERE status = #{status}")
    Long countOrdersByStatus(@Param("status") String status);
    
    /**
     * 获取订单统计数据
     */
    @Select("SELECT DATE(created_at) as date, COUNT(*) as count, SUM(actual_amount) as amount " +
            "FROM orders " +
            "WHERE created_at BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE(created_at) " +
            "ORDER BY date")
    List<Map<String, Object>> getOrderStatistics(@Param("startDate") String startDate, @Param("endDate") String endDate);
}