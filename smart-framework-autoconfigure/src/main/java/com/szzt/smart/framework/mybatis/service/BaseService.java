package com.szzt.smart.framework.mybatis.service;

import java.lang.reflect.Method;
import java.util.*;

import com.szzt.smart.framework.constant.CommonConstant;
import com.szzt.smart.framework.mybatis.entity.PageResult;
import com.szzt.smart.framework.mybatis.entity.StringKeyBaseEntity;
import jline.internal.Log;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.RowBounds;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.szzt.smart.framework.mybatis.entity.BaseEntity;
import com.szzt.smart.framework.mybatis.mapper.FrameworkBaseMapper;

import org.springframework.beans.factory.annotation.Value;
import tk.mybatis.mapper.entity.Example;

/**
 * 基础服务Service，每个业务服务类建议都继承该类;
 * 
 * @author mealkey
 * @version [版本号, 2017年2月9日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Slf4j
public abstract class BaseService<T>
{
    protected abstract FrameworkBaseMapper<T> getMapper();
    
    // ID生成策略，目前包括三种,auto(自动生成),UUID,Sequence,默认为auto
    @Value("${smartMybatis.idStrategy}")
    private String idStrategy = CommonConstant.ID_STRATEGY_AUTO;
    
    public T get(Object id)
    {
        return getMapper().selectByPrimaryKey(id);
    }
    
    public T selectOne(T record)
    {
        return getMapper().selectOne(record);
    }
    
    public List<T> select(Map<String, Object> paramMap, Class<T> recordClazz)
    {
        try
        {
            T record = recordClazz.newInstance();
            BeanUtils.populate(record, paramMap);
            return getMapper().select(record);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public int selectCount(T record)
    {
        return getMapper().selectCount(record);
    }
    
    public int selectCountByExample(Example example)
    {
        return getMapper().selectCountByExample(example);
    }
    
    public List<T> select(T record)
    {
        return getMapper().select(record);
    }
    
    public List<T> select(Example example)
    {
        return getMapper().selectByExample(example);
    }
    
    public PageResult selectPage(Example example, Integer pageNum, Integer pageSize)
    {
        Page<T> page = PageHelper.startPage(pageNum, pageSize);
        getMapper().selectByExample(example);
        PageResult pageResult = new PageResult(page.getPageSize(), page.getPageNum(), page.getTotal(), page.getPages(),
            page.getResult());
        return pageResult;
    }
    
    public PageResult selectPage(Map<String, Object> params, Integer pageNum, Integer pageSize, String methodName)
        throws Exception
    {
        Page page = PageHelper.startPage(pageNum, pageSize);
        // getMapper().selectByExample(example);
        getMapper().getClass().getMethod(methodName, params.getClass()).invoke(getMapper(), params);
        PageResult pageResult = new PageResult(page.getPageSize(), page.getPageNum(), page.getTotal(), page.getPages(),
            page.getResult());
        return pageResult;
    }
    
    public PageResult selectPage(Object params, Integer pageNum, Integer pageSize, String methodName)
        throws Exception
    {
        Page page = PageHelper.startPage(pageNum, pageSize);
        // getMapper().selectByExample(example);
        getMapper().getClass().getMethod(methodName, params.getClass()).invoke(getMapper(), params);
        PageResult pageResult = new PageResult(page.getPageSize(), page.getPageNum(), page.getTotal(), page.getPages(),
            page.getResult());
        return pageResult;
    }
    
    /**
     * 根据主键字符串进行查询，类中只有存在一个带有@Id注解的字段
     *
     * @param ids
     * @return[参数、异常说明] @return List<T> [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public List<T> selectByIds(String ids)
    {
        return getMapper().selectByIds(ids);
    }
    
    /**
     * 根据主键字符串进行查询，类中只有存在一个带有@Id注解的字段
     * 
     * @param ids
     * @return List<T>
     */
    public List<T> selectByIds(List<Object> ids)
    {
        if (null != ids && ids.size() > 0)
        {
            String idStr = StringUtils.join(ids, ",");
            return getMapper().selectByIds(idStr);
        }
        return new ArrayList<>();
    }
    
    public void deleteById(Object id)
    {
        getMapper().deleteByPrimaryKey(id);
    }
    
    public void deleteByExample(Example example)
    {
        getMapper().deleteByExample(example);
    }
    
    /**
     * 主键字符串进行删除，类中只有存在一个带有@Id注解的字段
     *
     * @param ids
     * @return void [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public void deleteByIds(String ids)
    {
        getMapper().deleteByIds(ids);
    }
    
    /**
     * 主键字符串进行删除，类中只有存在一个带有@Id注解的字段
     * 
     * @param ids
     */
    public void deleteByIds(List<Object> ids)
    {
        if (null != ids && ids.size() > 0)
        {
            String idStr = StringUtils.join(ids, ",");
            getMapper().deleteByIds(idStr);
        }
    }
    
    /**
     * 逻辑删除
     * 
     * @param id
     */
    public void deletedLogic(Object id, Class<T> recordClazz)
    {
        try
        {
            T record = recordClazz.newInstance();
            Method setIsDelete = record.getClass().getDeclaredMethod("setIsDelete", new Class[] {Boolean.class});
            Method setId = record.getClass().getDeclaredMethod("setId", new Class[] {Object.class});
            setIsDelete.invoke(record, true);
            setId.invoke(record, id);
            
            // 逻辑删除
            getMapper().updateByPrimaryKeySelective(record);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public int insert(T record)
    {
        ensureKey(record);
        setCreateTime(record);
        setLastUpdateTime(record);
        return getMapper().insert(record);
    }
    
    private void setCreateTime(T record)
    {
        try
        {
            Method setCreateTime = record.getClass().getDeclaredMethod("setCreateTime", new Class[] {Date.class});
            setCreateTime.invoke(record, new Date());
        }
        catch (Exception e)
        {
            log.warn("entity" + record.getClass() + " has no setCreateTime method");
        }
    }
    
    private void setLastUpdateTime(T record)
    {
        try
        {
            Method setLastUpdateTime = record.getClass().getDeclaredMethod("setLastUpdateTime",
                new Class[] {Date.class});
            setLastUpdateTime.invoke(record, new Date());
        }
        catch (Exception e)
        {
            log.warn("entity" + record.getClass() + " has no setLastUpdateTime method");
        }
    }
    
    public void save(T record)
    {
        BaseEntity entity = (BaseEntity)record;
        if (entity.getId() != null)
        {
            getMapper().updateByPrimaryKey(record);
        }
        else
        {
            entity.setId(getMapper().getKey(System.identityHashCode(entity)));
            getMapper().insert(record);
        }
    }
    
    protected void ensureKey(T record)
    {
        BaseEntity entity = (BaseEntity)record;
        if (entity.getId() != null)
        {
            return;
        }
        // 如果是自动生成则不做任何处理
        if (CommonConstant.ID_STRATEGY_AUTO.equals(idStrategy))
        {
            return;
        }
        // 如果是序列生成，则调用对应的序列生成
        else if (CommonConstant.ID_STRATEGY_SEQUENCE.equals(idStrategy))
        {
            entity.setId(getMapper().getKey(System.identityHashCode(entity)));
        }
        else if (CommonConstant.ID_STRATEGY_UUID.equals(idStrategy) && entity instanceof StringKeyBaseEntity)
        {
            StringKeyBaseEntity stringKeyBaseEntity = (StringKeyBaseEntity)entity;
            stringKeyBaseEntity.setId(uuid());
        }
    }
    
    /**
     * 封装JDK自带的UUID, 通过Random数字生成, 中间无-分割.
     */
    public String uuid()
    {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
    /**
     * 批量新增
     *
     * @param recordList 【记录列表】
     * @param useGeneratedKeys 【是否使用自增主键】
     * @return[参数、异常说明] @return int [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public int batchInsert(List<T> recordList, boolean useGeneratedKeys)
    {
        if (recordList == null || recordList.size() == 0)
        {
            return 0;
        }
        if (!useGeneratedKeys)
        {
            for (T record : recordList)
            {
                ensureKey(record);
            }
            return getMapper().insertListUnUseGeneratedKeys(recordList);
        }
        return getMapper().insertList(recordList);
    }
    
    /**
     * 批量新增（默认不是用自增序列）
     *
     * @param recordList
     * @return[参数、异常说明] @return int [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public int batchInsert(List<T> recordList)
    {
        if (recordList == null || recordList.size() == 0)
        {
            return 0;
        }
        
        return batchInsert(recordList, false);
    }
    
    /**
     * 说明：根据主键更新属性不为null的值
     *
     * @param record
     * @return[参数、异常说明] @return int [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public int update(T record)
    {
        setLastUpdateTime(record);
        return getMapper().updateByPrimaryKeySelective(record);
    }
    
    public int batchUpdate(List<T> recordList)
    {
        if (recordList == null || recordList.size() == 0)
        {
            return 0;
        }
        return getMapper().updateList(recordList);
    }
    
    /**
     * 分页查询
     * 
     * @param record
     * @param offset (从0开始)
     * @param limit （等于0不分页）
     * @return List<T>
     */
    public List<T> selectByRowBounds(T record, int offset, int limit)
    {
        RowBounds rowBounds = new RowBounds(offset, limit);
        return getMapper().selectByRowBounds(record, rowBounds);
    }
    
}
