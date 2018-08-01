package com.szzt.smart.framework.metrics;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

import lombok.Data;

@Data
public class InfluxdbConfig
{
    
    private static final int DEFAULT_PORT = 8086;
    
    private static final String DEFAULT_DB_NAME = "inFluxDB_Metrics";
    
    private static final String DEFAULT_HOST = "localhost";
    
    @NotBlank(message = "dbName 不能为空!")
    private String dbName = DEFAULT_DB_NAME;
    
    @NotBlank(message = "host 不能为空!")
    private String host = DEFAULT_HOST;
    
    @NotNull
    private Integer port = DEFAULT_PORT;
    
    private String userName = "";
    
    private String password = "";
    
}
