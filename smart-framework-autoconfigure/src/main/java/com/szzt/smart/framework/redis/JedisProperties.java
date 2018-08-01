package com.szzt.smart.framework.redis;

import java.util.function.Supplier;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@ConfigurationProperties(prefix = "redis")
public class JedisProperties {


  public static final int DEFAULT_TIMEOUT = 2000;
  public static final Supplier<JedisPoolProperties> DEFAULT_POOL = JedisPoolProperties::new;

  // 连接地址 ip:port
  @NotNull
  private String node;
  // 超时时间
  private int commandTimeout = DEFAULT_TIMEOUT;
  // 连接池配置
  private JedisPoolProperties pool = DEFAULT_POOL.get();
}
