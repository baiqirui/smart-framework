package com.szzt.smart.framework.redis;

import lombok.Data;

@Data
public class JedisPoolProperties
{

  public static final int DEFAULT_MAX_TOTAL = 256;
  public static final int DEFAULT_MAX_IDLE = 32;
  public static final int DEFAULT_MIN_IDLE = 8;
  public static final int DEFAULT_MAX_WAIT_MILLIS = 1000;
  public static final boolean DEFAULT_TEST_ON_BORROW = true;
  public static final boolean DEFAULT_TEST_ON_RETURN = false;
  public static final boolean DEFAULT_TEST_WHILE_IDLE = false;
  public static final int DEFAULT_NUM_TESTS_PER_EVICTION_RUN = -1;

  // 最大连接数
  private int maxTotal = DEFAULT_MAX_TOTAL;
  // 最大空闲数
  private int maxIdle = DEFAULT_MAX_IDLE;
  // 最小空闲数
  private int minIdle = DEFAULT_MIN_IDLE;
  // 最大等待时间
  private int maxWaitMillis = DEFAULT_MAX_WAIT_MILLIS;
  // 是否测试连接
  private boolean testOnBorrow = DEFAULT_TEST_ON_BORROW;
  // 是否对象返回时测试
  private boolean testOnReturn = DEFAULT_TEST_ON_RETURN;

  private boolean testWhileIdle = DEFAULT_TEST_WHILE_IDLE;

  private int numTestsPerEvictionRun = DEFAULT_NUM_TESTS_PER_EVICTION_RUN;


}
