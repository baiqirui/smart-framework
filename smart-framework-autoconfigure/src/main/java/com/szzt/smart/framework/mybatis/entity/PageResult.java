package com.szzt.smart.framework.mybatis.entity;

import java.io.Serializable;

import lombok.Data;

/**
 * 分页响应参数
 *
 * @author baiqirui
 * @version [版本号, 2017年8月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Data
public class PageResult<T> implements Serializable
{
    // 分页条数
    private int pageSize;
    
    // 当前页数
    private int pageNum;
    
    // 总条数
    private long totalNum;
    
    // 总页数
    private long totalPage;
    
    // 结果集
    private T data;
    
    public PageResult(int pageSize, int pageNum, long totalNum, T data)
    {
        // 计算总页数;
        this(pageSize, pageNum, totalNum, (int)(totalNum + pageSize - 1) / pageSize, data);
    }
    
    public PageResult(int pageSize, int pageNum, long totalNum, int totalPage, T data)
    {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.totalNum = totalNum;
        this.data = data;
        this.totalPage = totalPage;
    }
}
