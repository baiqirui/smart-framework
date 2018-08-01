package com.szzt.smart.framework.redis;

import redis.clients.jedis.JedisPoolConfig;

public class JedisPoolConfigHelper {

  public static JedisPoolConfig createJedisPoolConfig(JedisPoolProperties poolProperties){
    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
    jedisPoolConfig.setMaxTotal(poolProperties.getMaxTotal());
    jedisPoolConfig.setMaxIdle(poolProperties.getMaxIdle());
    jedisPoolConfig.setMinIdle(poolProperties.getMinIdle());
    jedisPoolConfig.setMaxWaitMillis(poolProperties.getMaxWaitMillis());
    jedisPoolConfig.setTestOnBorrow(poolProperties.isTestOnBorrow());
    jedisPoolConfig.setTestOnReturn(poolProperties.isTestOnReturn());
    jedisPoolConfig.setTestWhileIdle(poolProperties.isTestWhileIdle());
    jedisPoolConfig.setNumTestsPerEvictionRun(poolProperties.getNumTestsPerEvictionRun());
    return jedisPoolConfig;
  }

}
