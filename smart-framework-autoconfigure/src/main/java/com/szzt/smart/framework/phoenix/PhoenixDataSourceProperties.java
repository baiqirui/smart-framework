package com.szzt.smart.framework.phoenix;

import lombok.Data;

/**
 * phoenix配置属性
 *
 * @author
 * @Date 2017-10-12 16:59
 */
@Data
public class PhoenixDataSourceProperties
{
    private String url;
    
    private String type = "com.alibaba.druid.pool.DruidDataSource";
    
    private String driverClassName = "org.apache.phoenix.jdbc.PhoenixDriver";
    
    private String username;
    
    private String password;

    private int initialSize = 5;

    private int minIdle = 5;

    private int maxActive = 20;

    private int maxWait = 10000;

    private int timeBetweenEvictionRunsMillis = 60000;

    private int minEvictableIdleTimeMillis = 300000;

    private boolean testWhileIdle = true;

    private boolean testOnBorrow = false;

    private boolean testOnReturn = false;

    private String DEFAULT_JDBC_INTERCEPTORS = "SlowQueryReport(threshold=300)";

    private String validationQuery = "SELECT 'x';";

    private PhoenixMybatisProperties mybatis;
}
