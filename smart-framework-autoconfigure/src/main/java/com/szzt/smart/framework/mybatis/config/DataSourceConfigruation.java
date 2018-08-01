package com.szzt.smart.framework.mybatis.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import com.alibaba.druid.pool.DruidDataSource;
import com.szzt.smart.framework.ConditionalOnMapProperty;
import com.szzt.smart.framework.util.EnvironmentUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnMapProperty(prefix = "datasource.")
@ConditionalOnClass({DruidDataSource.class, SqlSessionFactory.class})
public class DataSourceConfigruation implements EnvironmentAware, BeanDefinitionRegistryPostProcessor
{
    private ConfigurableEnvironment environment;
    
    private BeanDefinitionRegistry beanDefinitionRegistry;
    
    private static final String WRITE_DS = "writeDS";
    
    private static final String READ_DS = "readDS";
    
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
            MultiDataSourceProperties multiDataSourceProperties = EnvironmentUtil
                .resolverSetting(MultiDataSourceProperties.class, "", environment, "datasource 配置");
            
            DataSource dynamicDataSource = createDataSource(multiDataSourceProperties);
            
            // 创建mybatis的事务管理器
            DataSourceTransactionManager mysqlTransactionManager = mysqlTransactionManager(dynamicDataSource);
            
            // 初始化SqlSessionFactory，并设置相关配置参数(luMybatis前缀的配置参数);
            SqlSessionFactory mySqlSessionFactory = mySqlSessionFactory(dynamicDataSource,
                multiDataSourceProperties.getSmartMybatis());
            
            // 将数据源，事务管理器，SqlSessionFactory注册到springbean;
            beanFactory.registerSingleton("dynamicDataSource", dynamicDataSource);
            beanFactory.registerSingleton("mysqlTransactionManager", mysqlTransactionManager);
            beanFactory.registerSingleton("mySqlSessionFactory", mySqlSessionFactory);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
    }
    
    public DataSourceTransactionManager mysqlTransactionManager(DataSource dynamicDataSource)
    {
        return new DataSourceTransactionManager(dynamicDataSource);
    }
    
    public SqlSessionFactory mySqlSessionFactory(DataSource dynamicDataSource,
        SmartMybatisConfigProperties mybatisConfigProperties)
        throws Exception
    {
        // 初始化SqlSessionFactory，并设置相关配置参数(luMybatis前缀的配置参数);
        final SqlSessionFactoryBean mySqlSessionFactory = new SqlSessionFactoryBean();
        mySqlSessionFactory.setDataSource(dynamicDataSource);
        mySqlSessionFactory.setMapperLocations(
            new PathMatchingResourcePatternResolver().getResources(mybatisConfigProperties.getMapperLocation()));
        
        mySqlSessionFactory.setConfigLocation(
            new PathMatchingResourcePatternResolver().getResource(mybatisConfigProperties.getConfigLocation()));
        if (StringUtils.isNotBlank(mybatisConfigProperties.getAliasPackage()))
        {
            mySqlSessionFactory.setTypeAliasesPackage(mybatisConfigProperties.getAliasPackage());
        }
        
        // 初始化MapperScanner,并制定扫描的路径，以及绑定对应的SqlSessionFactory;
        MapperScannerConfigurer scannerConfigurer = new MapperScannerConfigurer();
        scannerConfigurer.setBasePackage(mybatisConfigProperties.getMapperScanner());
        scannerConfigurer.setSqlSessionFactoryBeanName("mySqlSessionFactory");
        scannerConfigurer.postProcessBeanDefinitionRegistry(beanDefinitionRegistry);
        return mySqlSessionFactory.getObject();
    }
    
    public DataSource createDataSource(MultiDataSourceProperties properties)
    {
        // 写数据源是必填项;
        if (null == properties || null == properties.getDatasource().get(WRITE_DS))
        {
            throw new IllegalArgumentException("Property 'writeDS' is required");
        }
        DynamicDataSource dynamicDataSource = new DynamicDataSource();
        
        // 创建写数据，并设置为默认数据源
        DataSource writeDataSource = createDruidDataSource(properties.getDatasource().get(WRITE_DS));
        dynamicDataSource.setDefaultTargetDataSource(writeDataSource);
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DataSourceType.WRITE.name(), writeDataSource);
        // 如果设置了读数据源，则再创建读数据源;
        if (null != properties.getDatasource().get(READ_DS))
        {
            DataSource readDataSource = createDruidDataSource(properties.getDatasource().get(READ_DS));
            targetDataSources.put(DataSourceType.READ.name(), readDataSource);
        }
        
        dynamicDataSource.setTargetDataSources(targetDataSources);
        dynamicDataSource.afterPropertiesSet();
        return dynamicDataSource;
        
    }
    
    /**
     * 默认创建DruidDataSource
     *
     * @param properties
     * @return
     */
    private DataSource createDruidDataSource(DataSourceProperties properties)
    {
        DruidDataSource datasource = new DruidDataSource();
        datasource.setUrl(properties.getUrl());
        datasource.setDriverClassName(properties.getDriverClassName());
        datasource.setUsername(properties.getUsername());
        datasource.setPassword(properties.getPassword());
        datasource.setInitialSize(properties.getInitialSize());
        datasource.setMinIdle(properties.getMinIdle());
        datasource.setMaxWait(properties.getMaxWait());
        datasource.setMaxActive(properties.getMaxActive());
        datasource.setValidationQuery(properties.getValidationQuery());
        datasource.setTimeBetweenConnectErrorMillis(properties.getTimeBetweenEvictionRunsMillis());
        datasource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
        datasource.setTestWhileIdle(properties.isTestWhileIdle());
        datasource.setTestOnBorrow(properties.isTestOnBorrow());
        datasource.setTestOnReturn(properties.isTestOnReturn());
        
        return datasource;
    }
}
