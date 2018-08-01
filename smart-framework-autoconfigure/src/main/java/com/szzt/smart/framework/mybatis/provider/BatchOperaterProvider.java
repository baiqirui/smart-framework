package com.szzt.smart.framework.mybatis.provider;

import java.util.Set;

import com.szzt.smart.framework.mybatis.util.FrameworkSQLHelper;
import org.apache.ibatis.mapping.MappedStatement;

import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;

public class BatchOperaterProvider extends MapperTemplate
{
    
    public BatchOperaterProvider(Class<?> mapperClass, MapperHelper mapperHelper)
    {
        super(mapperClass, mapperHelper);
    }
    
    /**
     * 批量修改
     *
     * <!--示例--> <update id="batchUpdateDish" parameterType="java.util.Map"> <foreach collection="dishList"
     * item="dish" index="index" open="" close="" separator=";"> UPDATE DISH <set> <if
     * test="null!=dish.name">NAME=#{dish.name},PINYIN=#{dish.pinyin},</if> <if
     * test="null!=dish.statusId and 0!=dish.statusId">STATUS_ID=#{dish.statusId},</if> <if
     * test="null!=dish.tenantObjectChangeId and 0!=dish.tenantObjectChangeId"
     * >TENANT_OBJECT_CHANGE_ID=#{dish.tenantObjectChangeId},</if> <if
     * test="null!=dish.isDelete">IS_DELETE=#{dish.isDelete},</if> <if
     * test="null!=dish.ordinal">ORDINAL=#{ordinal},</if> MODIFIED_DATE = CURRENT_TIMESTAMP(),
     * MODIFIER_ID=#{modifyUserId} </set> where ID=#{dish.id} </foreach> </update>
     * 
     * @param ms
     */
    public String updateList(MappedStatement ms)
    {
        final Class<?> entityClass = getEntityClass(ms);
        // 开始拼sql
        StringBuilder sql = new StringBuilder();
        sql.append("<foreach collection=\"list\" item=\"record\"  index=\"index\"  separator=\";\" >");
        sql.append(FrameworkSQLHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(FrameworkSQLHelper.updateSetColumns(entityClass, "record", true, isNotEmpty()));
        sql.append(FrameworkSQLHelper.wherePKColumns(entityClass, "record"));
        sql.append("</foreach>");
        return sql.toString();
    }
    
    /**
     * 批量插入（不使用自增主键）
     *
     * @param ms
     * @return[参数、异常说明]
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public String insertListUnUseGeneratedKeys(MappedStatement ms)
    {
        final Class<?> entityClass = getEntityClass(ms);
        // 开始拼sql
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.insertIntoTable(entityClass, tableName(entityClass)));
        sql.append(SqlHelper.insertColumns(entityClass, false, false, false));
        sql.append(" VALUES ");
        sql.append("<foreach collection=\"list\" item=\"record\" separator=\",\" >");
        sql.append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        // 获取全部列
        Set<EntityColumn> columnList = EntityHelper.getColumns(entityClass);
        for (EntityColumn column : columnList)
        {
            sql.append(column.getColumnHolder("record") + ",");
        }
        sql.append("</trim>");
        sql.append("</foreach>");
        return sql.toString();
    }
}
