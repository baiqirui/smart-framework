/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2017 abel533@gmail.com
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

package com.szzt.smart.framework.mapper;

import java.util.List;

import javax.annotation.PostConstruct;

import com.alibaba.druid.pool.DruidDataSource;
import com.szzt.smart.framework.ConditionalOnMapProperty;
import com.szzt.smart.framework.mybatis.config.DataSourceConfiguration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import tk.mybatis.mapper.autoconfigure.MapperProperties;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;

/**
 * Mapper 配置
 *
 * @author liuzh
 */
@Configuration
@ConditionalOnMapProperty(prefix = "mapper")
@EnableConfigurationProperties(MapperProperties.class)
@ConditionalOnClass({DruidDataSource.class, SqlSessionFactory.class})
@AutoConfigureAfter(DataSourceConfiguration.class)
public class SmartMapperAutoConfiguration {

    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private MapperProperties properties;

    @PostConstruct
    public void addPageInterceptor() {
        MapperHelper mapperHelper = new MapperHelper();
        mapperHelper.setConfig(properties);
        if (properties.getMappers().size() > 0) {
            for (Class mapper : properties.getMappers()) {
                //提前初始化MapperFactoryBean,注册mappedStatements
                applicationContext.getBeansOfType(mapper);
                mapperHelper.registerMapper(mapper);
            }
        } else {
            //提前初始化MapperFactoryBean,注册mappedStatements
            applicationContext.getBeansOfType(Mapper.class);
            mapperHelper.registerMapper(Mapper.class);
        }
        for (SqlSessionFactory sqlSessionFactory : sqlSessionFactoryList) {
            mapperHelper.processConfiguration(sqlSessionFactory.getConfiguration());
        }
    }
}
