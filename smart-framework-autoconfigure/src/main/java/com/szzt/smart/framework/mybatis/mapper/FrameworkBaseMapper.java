/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2016 abel533@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.szzt.smart.framework.mybatis.mapper;

import java.util.List;

import com.szzt.smart.framework.mybatis.provider.BatchOperaterProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.UpdateProvider;


import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * 提供框架默认基础Mapper
 *
 * @author  baiqirui
 * @version  [版本号, 2017年2月14日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public interface FrameworkBaseMapper<T> extends Mapper<T>, MySqlMapper<T>,IdsMapper<T>, PKSqlMapper<T>
{
    // TODO
    // FIXME 特别注意，该接口不能被扫描到，否则会出错
    /**
     * 批量修改
     *
     * @param recordList
     * @return
     */
    @UpdateProvider(type = BatchOperaterProvider.class, method = "dynamicSQL")
    int updateList(List<T> recordList);
    
    /**
     * 批量新增;
     *
     * @param recordList
     * @return[参数、异常说明]
     * @return int [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    @InsertProvider(type = BatchOperaterProvider.class, method = "dynamicSQL")
    int insertListUnUseGeneratedKeys(List<T> recordList);
}
