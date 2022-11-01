package com.szzt.smart.framework.mybatis.config;

import lombok.Data;

@Data
public class DataSourceProperties
{
    private String url;
    
    private String username;
    
    private String password;
    
    private String driverClassName = "com.mysql.cj.jdbc.Driver";

    private String type = "com.alibaba.druid.pool.DruidDataSource";

    private int initialSize = 5;

    private int minIdle = 5;

    private int maxActive = 20;

    private int maxWait = 30000;

    private int timeBetweenEvictionRunsMillis = 60000;

    private int minEvictableIdleTimeMillis = 300000;

    private String validationQuery = "SELECT 'x';";

    private boolean testWhileIdle = true;

    private boolean testOnBorrow = false;

    private boolean testOnReturn = false;

    private String DEFAULT_JDBC_INTERCEPTORS = "SlowQueryReport(threshold=300)";
}
