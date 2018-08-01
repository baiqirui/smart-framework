package com.szzt.smart.framework.mybatis.config;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * 动态数据源;
 */
public class DynamicDataSource extends AbstractRoutingDataSource
{
    
    private DataSource writeDataSource;
    
    private DataSource readDataSource;
    
    public DataSource getWriteDataSource()
    {
        return writeDataSource;
    }
    
    public void setWriteDataSource(DataSource writeDataSource)
    {
        this.writeDataSource = writeDataSource;
    }
    
    public DataSource getReadDataSource()
    {
        return readDataSource;
    }
    
    public void setReadDataSource(DataSource readDataSource)
    {
        this.readDataSource = readDataSource;
    }
    
    @Override
    protected Object determineCurrentLookupKey()
    {
        DataSourceType type = DynamicDataSourceHolder.getDataSource();
        
        if (type == null || type == DataSourceType.WRITE)
        {
            return DataSourceType.WRITE.name();
        }
        return DataSourceType.READ.name();
    }
    
}
