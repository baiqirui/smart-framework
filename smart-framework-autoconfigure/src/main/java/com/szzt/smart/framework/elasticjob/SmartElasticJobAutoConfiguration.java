package com.szzt.smart.framework.elasticjob;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import com.szzt.smart.framework.mybatis.config.DataSourceConfigruation;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import com.dangdang.ddframe.job.api.ElasticJob;
import com.dangdang.ddframe.job.api.dataflow.DataflowJob;
import com.dangdang.ddframe.job.api.script.ScriptJob;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.szzt.smart.framework.ConditionalOnMapProperty;
import com.szzt.smart.framework.util.EnvironmentUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Hikaru on 17/9/6.
 */
@Configuration
@ConditionalOnMapProperty(prefix = SmartElasticJobAutoConfiguration.PREFIX)
@ConditionalOnClass({ZookeeperRegistryCenter.class, LiteJobConfiguration.class})
@AutoConfigureAfter(DataSourceConfigruation.class)
@Slf4j
public class SmartElasticJobAutoConfiguration implements ApplicationContextAware, EnvironmentAware
{
    
    public final static String PREFIX = "smartElasticJob";
    
    private ApplicationContext applicationContext;
    
    private ConfigurableEnvironment environment;
    
    protected void createBean(ZookeeperRegistryCenter registryCenter, JobEventConfiguration jobEventConfiguration,
        String[] elasticJobs)
    {
        if (ArrayUtils.isNotEmpty(elasticJobs))
        {
            Arrays.stream(elasticJobs).forEach(s -> {
                try
                {
                    ElasticJob elasticJob = this.applicationContext.getBean(s, ElasticJob.class);
                    SmartElasticJob smartElasticJob = elasticJob.getClass().getAnnotation(SmartElasticJob.class);
                    LiteJobConfiguration liteJobConfiguration;
                    if (elasticJob instanceof SimpleJob)
                    {
                        liteJobConfiguration = createSimpleJobLiteJobConfiguration(elasticJob, smartElasticJob);
                    }
                    else if (elasticJob instanceof DataflowJob)
                    {
                        liteJobConfiguration = createDataFlowJobConfiguration(elasticJob, smartElasticJob);
                    }
                    else if (elasticJob instanceof ScriptJob)
                    {
                        liteJobConfiguration = createScriptJobConfiguration(elasticJob, smartElasticJob);
                    }
                    else
                    {
                        throw new IllegalArgumentException("error elasticJob type");
                    }
                    Preconditions.checkNotNull(liteJobConfiguration, "liteJobConfiguration不能为空");
                    ElasticJobListener[] elasticJobListeners = transElasticJobListeners(
                        smartElasticJob.elasticJobListeners());
                    List list = new ArrayList();
                    if (elasticJob instanceof ScriptJob)
                    {
                        list.add(null);
                    }
                    else
                    {
                        list.add(elasticJob);
                    }
                    list.add(registryCenter);
                    list.add(liteJobConfiguration);
                    Optional.ofNullable(jobEventConfiguration).ifPresent(j -> list.add(j));
                    list.add(elasticJobListeners);
                    createSpringJobScheduler(elasticJob.getClass().getSimpleName() + "jobScheduler", list);
                    
                }
                catch (Exception e)
                {
                    throw Throwables.propagate(e);
                }
                
            });
        }
    }
    
    public void createSpringJobScheduler(String name, List list)
    {
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext)applicationContext;
        BeanDefinitionRegistry beanDefinitionRegistry = (BeanDefinitionRegistry)configurableApplicationContext
            .getBeanFactory();
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(SpringJobScheduler.class);
        builder.setInitMethodName("init");
        for (Object obj : list)
        {
            builder.addConstructorArgValue(obj);
        }
        beanDefinitionRegistry.registerBeanDefinition(name, builder.getBeanDefinition());
        this.applicationContext.getBean(name, SpringJobScheduler.class);
        log.info("spring bean name {} register success ", name);
    }
    
    protected ElasticJobListener[] transElasticJobListeners(Class<? extends ElasticJobListener>[] clazzs)
    {
        ElasticJobListener[] es = new ElasticJobListener[] {};
        if (ArrayUtils.isNotEmpty(clazzs))
        {
            for (int i = 0, len = clazzs.length; i < len; i++)
            {
                es = ArrayUtils.add(es, this.applicationContext.getBean(clazzs[i]));
            }
        }
        if (this.applicationContext.containsBean("elasticJobSleuthListener"))
        {
            es = ArrayUtils.add(es, (ElasticJobListener)this.applicationContext.getBean("elasticJobSleuthListener"));
        }
        return es;
    }
    
    protected LiteJobConfiguration createSimpleJobLiteJobConfiguration(ElasticJob elasticJob,
        SmartElasticJob smartElasticJob)
    {
        return LiteJobConfiguration
            .newBuilder(new SimpleJobConfiguration(JobCoreConfiguration
                .newBuilder(elasticJob.getClass().getName(),
                    cronValue(smartElasticJob.cron()),
                    smartElasticJob.shardingTotalCount())
                .shardingItemParameters(Strings.emptyToNull(smartElasticJob.shardingItemParameters()))
                .build(), elasticJob.getClass().getCanonicalName()))
            .overwrite(smartElasticJob.overwrite())
            .disabled(smartElasticJob.disabled())
            .jobShardingStrategyClass(Strings.emptyToNull(smartElasticJob.jobShardingStrategyClass()))
            .build();
    }
    
    protected LiteJobConfiguration createDataFlowJobConfiguration(ElasticJob elasticJob, SmartElasticJob hnElasticJob)
    {
        return LiteJobConfiguration
            .newBuilder(new DataflowJobConfiguration(JobCoreConfiguration
                .newBuilder(elasticJob.getClass().getName(),
                    cronValue(hnElasticJob.cron()),
                    hnElasticJob.shardingTotalCount())
                .shardingItemParameters(Strings.emptyToNull(hnElasticJob.shardingItemParameters()))
                .build(), elasticJob.getClass().getCanonicalName(), hnElasticJob.streamingProcess()))
            .overwrite(hnElasticJob.overwrite())
            .disabled(hnElasticJob.disabled())
            .jobShardingStrategyClass(Strings.emptyToNull(hnElasticJob.jobShardingStrategyClass()))
            .build();
    }
    
    protected LiteJobConfiguration createScriptJobConfiguration(ElasticJob elasticJob, SmartElasticJob hnElasticJob)
    {
        return LiteJobConfiguration
            .newBuilder(new ScriptJobConfiguration(JobCoreConfiguration
                .newBuilder(elasticJob.getClass().getName(),
                    cronValue(hnElasticJob.cron()),
                    hnElasticJob.shardingTotalCount())
                .shardingItemParameters(Strings.emptyToNull(hnElasticJob.shardingItemParameters()))
                .build(), hnElasticJob.scriptCommandLine()))
            .overwrite(hnElasticJob.overwrite())
            .disabled(hnElasticJob.disabled())
            .jobShardingStrategyClass(Strings.emptyToNull(hnElasticJob.jobShardingStrategyClass()))
            .build();
    }
    
    public ZookeeperRegistryCenter regCenter(SmartElasticJobProperties hnElasticJobProperties)
    {
        ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(
            hnElasticJobProperties.getServerList(), hnElasticJobProperties.getNamespace());
        BeanUtils.copyProperties(hnElasticJobProperties, zookeeperConfiguration, "serverLists", "namespace");
        ZookeeperRegistryCenter regCenter = new ZookeeperRegistryCenter(zookeeperConfiguration);
        regCenter.init();
        return regCenter;
    }
    
    protected String cronValue(String cron)
    {
        if (cron.startsWith("${") && cron.endsWith("}"))
        {
            return this.environment.getProperty(StringUtils.removeEnd(StringUtils.removeStart(cron, "${"), "}"));
        }
        return cron;
    }
    
    @Override
    public void setEnvironment(Environment environment)
    {
        this.environment = (ConfigurableEnvironment)environment;
    }
    
    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException
    {
        this.applicationContext = applicationContext;
        SmartElasticJobProperties hnElasticJobProperties = EnvironmentUtil
            .resolverSetting(SmartElasticJobProperties.class, PREFIX, environment, "elastic-job配置");
        
        String[] jobs = this.applicationContext.getBeanNamesForAnnotation(SmartElasticJob.class);
        ZookeeperRegistryCenter registryCenter = regCenter(hnElasticJobProperties);
        JobEventConfiguration jobEventConfiguration = Optional
            .ofNullable(Strings.emptyToNull(hnElasticJobProperties.getDataSource()))
            .map(s -> new JobEventRdbConfiguration(applicationContext.getBean(s + "Ds", DataSource.class))
            
            )
            .orElse(null);
        createBean(registryCenter, jobEventConfiguration, jobs);
        
    }
    
}