package com.szzt.smart.framework.redis;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.szzt.smart.framework.ConditionalOnMapProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import redis.clients.jedis.HostAndPort;

@Slf4j
@Configuration
@EnableCaching
@ConditionalOnMapProperty(prefix = "redis.")
@ConditionalOnExpression("#{environment.containsProperty('redis.cluster-nodes')}")
@ConditionalOnClass(RedisTemplate.class)
@AutoConfigureAfter({JedisClusterConfiguration.class})
@EnableConfigurationProperties(JedisClusterProperties.class)
public class JedisCacheConfiguration extends CachingConfigurerSupport
{
    
    @Autowired
    private JedisClusterProperties jedisClusterProperties;
    
    @Bean
    @ConditionalOnMissingBean
    public KeyGenerator keyGenerator()
    {
        return (target, method, params) -> String.join("",
            target.getClass().getName(),
            method.getName(),
            Arrays.stream(params).map(Object::toString).collect(Collectors.joining("")));
    }
    
    @Bean(name = "springCacheManager")
    @ConditionalOnMissingBean
    public CacheManager cacheManager(RedisTemplate redisTemplate)
    {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        redisCacheManager.setDefaultExpiration(jedisClusterProperties.getExpireSeconds());
        if (!jedisClusterProperties.getExpires().isEmpty())
        {
            redisCacheManager.setExpires(jedisClusterProperties.getExpires());
        }
        return redisCacheManager;
    }
    
    @Bean
    public RedisConnectionFactory getRedisConnectionFactory(Set<HostAndPort> hostAndPorts)
    {
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
        hostAndPorts.forEach(hostAndPort -> clusterConfiguration
            .addClusterNode(new RedisNode(hostAndPort.getHost(), hostAndPort.getPort())));
        return new JedisConnectionFactory(clusterConfiguration);
    }
    
    @ConditionalOnMissingBean
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory)
    {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.setSerializationInclusion(Include.NON_NULL);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
        jackson2JsonRedisSerializer.setObjectMapper(om);
        
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
