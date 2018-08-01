package com.szzt.smart.framework.zookeeper;

import com.szzt.smart.framework.ConditionalOnMapProperty;
import org.apache.curator.RetryPolicy;
import org.apache.curator.ensemble.EnsembleProvider;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.endpoint.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by Hikaru on 17/8/24.
 */
@Slf4j
@ConditionalOnMapProperty(prefix = "zookeeper.")
@ConditionalOnClass(CuratorFramework.class)
@ConditionalOnProperty(value = "zookeeper.enabled", matchIfMissing = true)
@EnableConfigurationProperties(LuZookeeperProperties.class)
@Configuration
public class LuZookeeperAutoConfiguration
{
    
    @Autowired(required = false)
    private EnsembleProvider ensembleProvider;
    
    @Bean(destroyMethod = "close")
    public CuratorFramework curatorFramework(RetryPolicy retryPolicy, LuZookeeperProperties properties)
        throws Exception
    {
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();
        if (this.ensembleProvider != null)
        {
            builder.ensembleProvider(this.ensembleProvider);
        }
        else
        {
            builder.connectString(properties.getConnectString());
        }
        CuratorFramework curator = builder.retryPolicy(retryPolicy).build();
        curator.start();
        log.trace("blocking until connected to zookeeper for " + properties.getBlockUntilConnectedWait()
            + properties.getBlockUntilConnectedUnit());
        curator.blockUntilConnected(properties.getBlockUntilConnectedWait(), properties.getBlockUntilConnectedUnit());
        log.trace("connected to zookeeper");
        return curator;
    }
    
    @Bean
    public RetryPolicy exponentialBackoffRetry(LuZookeeperProperties properties)
    {
        return new ExponentialBackoffRetry(properties.getBaseSleepTimeMs(), properties.getMaxRetries(),
            properties.getMaxSleepMs());
    }
    
    @Configuration
    @ConditionalOnClass(Endpoint.class)
    @ConditionalOnBean(CuratorFramework.class)
    protected static class ZookeeperHealthConfig
    {
        
        @SuppressWarnings("all")
        @Bean
        @ConditionalOnEnabledHealthIndicator("zookeeper")
        public LuZookeeperHealthIndicator zookeeperHealthIndicator(CuratorFramework curatorFramework)
        {
            return new LuZookeeperHealthIndicator(curatorFramework);
        }
    }
    
    @Bean
    public LuZookeeperOper hnZookeeperListener(CuratorFramework curatorFramework)
    {
        return new LuZookeeperOper(curatorFramework);
    }
    
}
