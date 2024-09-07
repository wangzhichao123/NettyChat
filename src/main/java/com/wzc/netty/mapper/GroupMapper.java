package com.wzc.netty.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wzc.netty.pojo.entity.Group;
import org.apache.ibatis.annotations.Param;

/**
* @author wzc
* @description 针对表【group】的数据库操作Mapper
* @createDate 2024-07-11 23:15:06
* @Entity generator.domain.Group
*/
public interface GroupMapper extends BaseMapper<Group> {

    /**
     * 查询群组信息
     * @param groupId
     * @return
     */
    Group queryGroupByGroupId(@Param("groupId") String groupId);
}




