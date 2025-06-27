package com.groupbuy.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.groupbuy.entity.GroupLeader;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface GroupLeaderMapper extends BaseMapper<GroupLeader> {
    
    @Select("SELECT COUNT(*) as orderCount, SUM(actual_amount) as totalAmount " +
            "FROM orders WHERE group_leader_id = #{groupLeaderId} AND status = 'COMPLETED'")
    Map<String, Object> getOrderStatistics(Long groupLeaderId);
}