package com.szzt.smart.framework.mybatis.util;

import java.util.Set;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

public class FrameworkSQLHelper extends SqlHelper
{
    
    /**
     * where主键条件
     *
     * @param entityClass
     * @return
     */
    public static String wherePKColumns(Class<?> entityClass, String entityName)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("<where>");
        // 获取全部列
        Set<EntityColumn> columnList = EntityHelper.getPKColumns(entityClass);
        // 当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnList)
        {
            sql.append(" AND "+ column.getColumnEqualsHolder(entityName));
        }
        sql.append("</where>");
        return sql.toString();
    }
    
}
