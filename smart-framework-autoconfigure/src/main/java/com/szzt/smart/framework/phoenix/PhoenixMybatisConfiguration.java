package com.szzt.smart.framework.phoenix;

import javax.sql.DataSource;

import com.szzt.smart.framework.mybatis.config.MultiDataSourceProperties;
import com.szzt.smart.framework.util.EnvironmentUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.phoenix.jdbc.PhoenixDriver;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.validation.BindException;

import com.alibaba.druid.pool.DruidDataSource;
import com.szzt.smart.framework.ConditionalOnMapProperty;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass({PhoenixDriver.class, SqlSessionFactory.class})
@ConditionalOnMapProperty(prefix = "phoenix.")
@ConditionalOnProperty(value = "phoenix.enable", matchIfMissing = true)
public class PhoenixMybatisConfiguration implements EnvironmentAware, BeanDefinitionRegistryPostProcessor
{
    
    private ConfigurableEnvironment environment;
    
    private BeanDefinitionRegistry beanDefinitionRegistry;
    
    @Override
    public void setEnvironment(Environment environment)
    {
        this.environment = (ConfigurableEnvironment)environment;
    }
    
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
        throws BeansException
    {
        this.beanDefinitionRegistry = registry;
    }
    
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
        throws BeansException
    {
        try
        {
            // 创建dataSource，并注入到spring中;
            PhoenixProperties phoenixProperties = EnvironmentUtil
                .resolverSetting(PhoenixProperties.class, "", environment, "phoenix datasource 配置");

            DataSource phoenixDataSource = createPhoenixDataSource(phoenixProperties.getPhoenix());
            
            // 创建mybatis的事务管理器
            DataSourceTransactionManager phoenixTransactionManager = phoenixTransactionManager(phoenixDataSource);
            
            // 初始化SqlSessionFactory，并设置相关配置参数(luMybatis前缀的配置参数);
            SqlSessionFactory phoenixSqlSessionFactory = phoenixSqlSessionFactory(phoenixDataSource,
                phoenixProperties.getPhoenix().getMybatis());
            
            // 将数据源，事务管理器，SqlSessionFactory注册到springbean;
            beanFactory.registerSingleton("phoenixDataSource", phoenixDataSource);
            beanFactory.registerSingleton("phoenixTransactionManager", phoenixTransactionManager);
            beanFactory.registerSingleton("phoenixSqlSessionFactory", phoenixSqlSessionFactory);
        }
        catch (Exception e)
        {
            log.error("load PhoenixMybatisConfiguration failed", e);
        }
        
    }
    
    /**
     * 默认创建DruidDataSource
     *
     * @param properties
     * @return
     */
    public DataSource createPhoenixDataSource(PhoenixDataSourceProperties properties)
    {
        DruidDataSource phoenixDataSource = new DruidDataSource();
        phoenixDataSource.setUrl(properties.getUrl());
        phoenixDataSource.setDriverClassName(properties.getDriverClassName());
        phoenixDataSource.setUsername(properties.getUsername());
        phoenixDataSource.setPassword(properties.getPassword());
        phoenixDataSource.setInitialSize(properties.getInitialSize());
        phoenixDataSource.setMinIdle(properties.getMinIdle());
        phoenixDataSource.setMaxWait(properties.getMaxWait());
        phoenixDataSource.setMaxActive(properties.getMaxActive());
        phoenixDataSource.setTimeBetweenConnectErrorMillis(properties.getTimeBetweenEvictionRunsMillis());
        phoenixDataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        phoenixDataSource.setTestWhileIdle(properties.isTestWhileIdle());
        phoenixDataSource.setTestOnBorrow(properties.isTestOnBorrow());
        phoenixDataSource.setTestOnReturn(properties.isTestOnReturn());
        // phoenixDataSource.setValidationQuery(properties.getValidationQuery());
        
        return phoenixDataSource;
    }
    
    public DataSourceTransactionManager phoenixTransactionManager(DataSource phoenixDataSource)
    {
        return new DataSourceTransactionManager(phoenixDataSource);
    }
    
    public SqlSessionFactory phoenixSqlSessionFactory(DataSource phoenixDataSource,
        PhoenixMybatisProperties phoenixMybatisProperties)
        throws Exception
    {
        // 初始化SqlSessionFactory，并设置相关配置参数(phoenix.mybatis前缀的配置参数);
        final SqlSessionFactoryBean phoenixSqlSessionFactory = new SqlSessionFactoryBean();
        phoenixSqlSessionFactory.setDataSource(phoenixDataSource);
        phoenixSqlSessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources(phoenixMybatisProperties.getMapperLocation()));
        if (StringUtils.isNotBlank(phoenixMybatisProperties.getConfigLocation()))
        {
            phoenixSqlSessionFactory.setConfigLocation(
                new PathMatchingResourcePatternResolver().getResource(phoenixMybatisProperties.getConfigLocation()));
        }
        if (StringUtils.isNotBlank(phoenixMybatisProperties.getAliasPackage()))
        {
            phoenixSqlSessionFactory.setTypeAliasesPackage(phoenixMybatisProperties.getAliasPackage());
        }
        
        // 初始化MapperScanner,并制定扫描的路径，以及绑定对应的SqlSessionFactory;
        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        scannerConfigurer.setBasePackage(phoenixMybatisProperties.getMapperScanner());
        scannerConfigurer.setSqlSessionFactoryBeanName("phoenixSqlSessionFactory");
        scannerConfigurer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry);
        return phoenixSqlSessionFactory.getObject();
    }
}