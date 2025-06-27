package com.groupbuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.groupbuy.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    
    /**
     * 根据角色查询用户列表
     */
    @Select("SELECT * FROM users WHERE role = #{role} AND status = 1")
    List<User> findByRole(String role);
    
    /**
     * 统计用户数量
     */
    @Select("SELECT COUNT(*) FROM users WHERE status = 1")
    Long countActiveUsers();
    
    /**
     * 根据创建时间范围查询用户
     */
    @Select("SELECT * FROM users WHERE created_at BETWEEN #{startTime} AND #{endTime}")
    List<User> findByCreateTimeRange(String startTime, String endTime);
}