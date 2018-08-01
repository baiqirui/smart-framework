package com.szzt.smart.framework.mybatis.provider;

import org.apache.ibatis.mapping.MappedStatement;

import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;


/**
 * Created by lvjj on 2017/2/7.
 */
public class PkSqlProvider extends MapperTemplate
{
    public PkSqlProvider(Class<?> mapperClass, MapperHelper mapperHelper)
    {
        super(mapperClass, mapperHelper);
    }
    
    public String getKey(MappedStatement ms)
    {
        Class<?> entityClass = getEntityClass(ms);
        String tableName = tableName(entityClass);
        String sql = "select %s.nextVal from dual where #{random} = #{random}";
        return String.format(sql, tableName);
    }
}
