package com.szzt.smart.framework.mybatis.config;

import java.util.Properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created on 2017/8/8.
 */
@Data
@ConfigurationProperties(prefix = "smart.pagehelper")
public class PageHelperProperties
{
    private String helperDialect = "mysql";
    
    private boolean offsetAsPageNum = false;
    
    private boolean rowBoundsWithCount = false;
    
    private boolean pageSizeZero = false;
    
    private boolean reasonable = false;
    
    private String params = "pageNum=pageNum;pageSize=pageSize;count=countSql;reasonable=reasonable;pageSizeZero=pageSizeZero";
    
    private boolean supportMethodsArguments = false;
    
    private boolean autoRuntimeDialect = false;
    
    private boolean closeConn = true;
    
    public Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("helperDialect", helperDialect);
        properties.put("offsetAsPageNum", offsetAsPageNum);
        properties.put("rowBoundsWithCount", rowBoundsWithCount);
        properties.put("pageSizeZero", pageSizeZero);
        properties.put("reasonable", reasonable);
        properties.put("params", params);
        properties.put("supportMethodsArguments", supportMethodsArguments);
        properties.put("autoRuntimeDialect", autoRuntimeDialect);
        properties.put("closeConn", closeConn);
        return properties;
    }
}
