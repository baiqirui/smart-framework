package com.szzt.smart.framework.sample.service;

import com.szzt.smart.framework.sample.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.szzt.smart.framework.mybatis.mapper.FrameworkBaseMapper;
import com.szzt.smart.framework.mybatis.service.BaseService;
import com.szzt.smart.framework.sample.entity.User;

/**
 * test
 *
 * @author
 * @Date 2017-10-13 13:48
 */
@Service
public class UserService extends BaseService<User>
{
    @Autowired
    private UserMapper userMapper;
    
    @Override
    protected FrameworkBaseMapper<User> getMapper()
    {
        return userMapper;
    }

    @Cacheable(value = "testUser", key = "#key.concat(#id)")
    public User getUser(Long id, String key)
    {
        User user = new User();
        user.setId(id);
        user.setAge(18);
        user.setName("xxxx");
        return user;
    }

}
