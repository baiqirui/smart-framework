package com.szzt.smart.framework.mybatis.config;


/**
 * 数据源现场变量;
 */
public final class DynamicDataSourceHolder
{
    
    private static final ThreadLocal<DataSourceType> holder = new ThreadLocal<DataSourceType>();
    
    private DynamicDataSourceHolder()
    {
        //
    }
    
    public static void putDataSource(DataSourceType dataSource)
    {
        holder.set(dataSource);
    }
    
    public static DataSourceType getDataSource()
    {
        return holder.get();
    }
    
    public static void clearDataSource()
    {
        holder.remove();
    }
    
}