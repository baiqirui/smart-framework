package com.szzt.smart.framework.sample.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.szzt.smart.framework.mybatis.mapper.FrameworkBaseMapper;
import com.szzt.smart.framework.sample.entity.User;

/**
 * test
 *
 * @author
 * @Date 2017-10-13 13:50
 */
@Repository
@Mapper
public interface UserMapper extends FrameworkBaseMapper<User>
{
}
