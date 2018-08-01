package com.szzt.smart.framework.mybatis.config;

import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInterceptor;

/**
 * Created on 2017/8/8.
 */
@Configuration
@ConditionalOnClass({PageHelper.class, SqlSessionFactory.class})
@EnableConfigurationProperties(PageHelperProperties.class)
@AutoConfigureAfter(DataSourceConfigruation.class)
public class PageHelperConfiguration
{
    
    @SuppressWarnings("All")
    @Autowired
    private List<SqlSessionFactory> sqlSessionFactoryList;
    
    @Autowired
    private PageHelperProperties pageHelperProperties;
    
    @PostConstruct
    public void addPageInterceptor()
    {
        PageInterceptor interceptor = new PageInterceptor();
        Properties properties = pageHelperProperties.getProperties();
        interceptor.setProperties(properties);
        sqlSessionFactoryList
            .forEach(sqlSessionFactory -> sqlSessionFactory.getConfiguration().addInterceptor(interceptor));
    }
}
