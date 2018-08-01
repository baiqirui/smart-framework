package com.szzt.smart.framework.redis;

import java.util.List;

import com.szzt.smart.framework.ConditionalOnMapProperty;
import com.szzt.smart.framework.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import redis.clients.jedis.JedisCluster;

@Service("redisService")
@ConditionalOnMapProperty(prefix = "redis.")
@ConditionalOnExpression("#{environment.containsProperty('redis.cluster-nodes')}")
@ConditionalOnClass(RedisTemplate.class)
@AutoConfigureAfter({JedisClusterConfiguration.class})
public class RedisService
{
    
    @Autowired
    private JedisCluster jedisCluster;
    
    public <T> String set(String key, T object)
    {
        String object2JsonString = JsonUtil.obj2string(object);
        String set = jedisCluster.set(key, object2JsonString);
        return set;
    }
    
    public <T> Long setNX(String key, T object)
    {
        String object2JsonString = JsonUtil.obj2string(object);
        Long ret = jedisCluster.setnx(key, object2JsonString);
        return ret;
    }
    
    public <T> String set(String key, T object, int expire)
    {
        String object2JsonString = JsonUtil.obj2string(object);
        String set = jedisCluster.setex(key, expire, object2JsonString);
        return set;
    }
    
    public <T> T get(String key, Class<T> clazz)
    {
        String value = get(key);
        return StringUtils.isBlank(value) ? null : JsonUtil.str2obj(value, clazz);
    }
    
    public String set(String key, String value)
    {
        String set = jedisCluster.set(key, value);
        return set;
    }
    
    public String set(String key, String value, int expire)
    {
        String set = jedisCluster.setex(key, expire, value);
        return set;
    }
    
    public String get(String key)
    {
        String string = jedisCluster.get(key);
        return string;
    }
    
    public <T> String set(String key, List<T> list)
    {
        String value = JsonUtil.obj2string(list);
        return set(key, value);
    }
    
    public <T> String set(String key, List<T> list, int expire)
    {
        String value = JsonUtil.obj2string(list);
        return set(key, value, expire);
    }
    
    public Long delete(String key)
    {
        Long del = jedisCluster.del(key);
        return del;
    }
    
    public long lpush(String key, Object obj)
    {
        return jedisCluster.lpush(key, JsonUtil.obj2string(obj));
    }
    
    public long rpush(String key, Object obj)
    {
        return jedisCluster.rpush(key, JsonUtil.obj2string(obj));
    }
    
    public Object lpop(String key)
    {
        return jedisCluster.lpop(key);
    }
    
    public boolean exists(String key)
    {
        return jedisCluster.exists(key);
    }
    
    public String getset(String key, String value)
    {
        return jedisCluster.getSet(key, value);
    }
    
    public Long incr(String key)
    {
        try
        {
            return jedisCluster.incr(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1l;
        }
    }
    
    public Long decr(String key)
    {
        try
        {
            return jedisCluster.decr(key);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return -1l;
        }
    }
    
    public void expire(String key, int expireTime)
    {
        jedisCluster.expire(key, expireTime);
    }
}