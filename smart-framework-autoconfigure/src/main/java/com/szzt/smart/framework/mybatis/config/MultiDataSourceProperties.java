package com.szzt.smart.framework.mybatis.config;

import java.util.Map;

import lombok.Data;

@Data
public class MultiDataSourceProperties
{
    private Map<String, DataSourceProperties> datasource;

    private SmartMybatisConfigProperties smartMybatis;
}
