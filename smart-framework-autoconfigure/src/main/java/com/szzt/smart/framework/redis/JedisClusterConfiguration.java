package com.szzt.smart.framework.redis;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.szzt.smart.framework.ConditionalOnMapProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

@Slf4j
@Configuration
@ConditionalOnMapProperty(prefix = "redis.")
@ConditionalOnExpression("#{environment.containsProperty('redis.cluster-nodes')}")
@ConditionalOnClass(JedisCluster.class)
@EnableConfigurationProperties(JedisClusterProperties.class)
public class JedisClusterConfiguration
{
    
    @Autowired
    private JedisClusterProperties jedisClusterProperties;
    
    @Bean
    public Set<HostAndPort> getNodes()
    {
        return Arrays.stream(jedisClusterProperties.getClusterNodes().split(",")).map(node -> {
            String[] hostAndPort = node.split(":");
            return new HostAndPort(hostAndPort[0].trim(), Integer.valueOf(hostAndPort[1].trim()));
        }).collect(Collectors.toSet());
    }
    
    @Bean
    public JedisPoolConfig getJedisPoolConfig()
    {
        return JedisPoolConfigHelper.createJedisPoolConfig(jedisClusterProperties.getPool());
    }
    
    @Bean
    @ConditionalOnMissingBean
    public JedisCluster JedisClusterFactory(JedisClusterProperties jedisClusterProperties,
        JedisPoolConfig jedisPoolConfig)
    {
        return new JedisCluster(getNodes(), jedisClusterProperties.getCommandTimeout(), jedisPoolConfig);
    }
}
