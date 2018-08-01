package com.szzt.smart.framework.web.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页请求参数
 *
 * @author baiqirui
 * @version [版本号, 2017年8月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Data
public class PageRequest implements Serializable
{
    //分页条数
    private  int pageSize = 10;

    //当前页数
    private int pageNum = 1;
}
