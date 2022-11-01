package com.szzt.smart.framework.mybatis.config;

import java.util.Map;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "smart.db") //这个地方其实没有作用，只是给idea插件提示属性使用
public class MultiDataSourceProperties
{
    private Map<String, DataSourceProperties> datasource;

    private SmartMybatisConfigProperties mybatisConfig;
}
