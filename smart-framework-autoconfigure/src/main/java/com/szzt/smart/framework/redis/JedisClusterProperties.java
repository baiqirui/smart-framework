package com.szzt.smart.framework.redis;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@Validated
@ConfigurationProperties(prefix = "redis")
public class JedisClusterProperties
{
    
    public static final int DEFAULT_TIMEOUT = 2000;
    
    public static final int DEFAULT_EXPIRE_SECONDS = 60 * 60 * 6;
    
    public static final Map<String, Long> DEFAULT_EXPIRES = new HashMap<>();
    
    public static final Supplier<JedisPoolProperties> DEFAULT_POOL = JedisPoolProperties::new;
    
    // 连接地址 ip:port
    @NotNull
    private String clusterNodes;
    
    // 超时时间
    private int commandTimeout = DEFAULT_TIMEOUT;
    
    // 过期时间
    private int expireSeconds = DEFAULT_EXPIRE_SECONDS;
    
    //
    private Map<String, Long> expires = DEFAULT_EXPIRES;
    
    // 连接池配置
    private JedisPoolProperties pool = DEFAULT_POOL.get();
}
