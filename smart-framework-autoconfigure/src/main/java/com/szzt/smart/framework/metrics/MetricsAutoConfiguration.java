package com.szzt.smart.framework.metrics;

import static com.szzt.smart.framework.metrics.MetricsConstants.PROP_METRIC_REG_JVM_GARBAGE;
import static com.szzt.smart.framework.metrics.MetricsConstants.PROP_METRIC_REG_JVM_MEMORY;
import static com.szzt.smart.framework.metrics.MetricsConstants.PROP_METRIC_REG_JVM_THREADS;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.actuate.autoconfigure.MetricsDropwizardAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;

@Configuration
@ConditionalOnClass(MetricRegistry.class)
@ConditionalOnWebApplication
@ConditionalOnProperty(value = "metrics.enabled", matchIfMissing = true)
@AutoConfigureBefore(MetricsDropwizardAutoConfiguration.class)
@EnableConfigurationProperties(MetricsProperties.class)
public class MetricsAutoConfiguration
{
    
    @Bean
    public FilterRegistrationBean metricFilter(MetricRegistry metricRegistry, MetricsProperties metricsProperties)
    {
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        MetricsFilter metricsFilter = new MetricsFilter(metricRegistry);
        registrationBean.setFilter(metricsFilter);
        if (CollectionUtils.isNotEmpty(metricsProperties.getUrlPatterns()))
        {
            // 设置映射路径
            registrationBean.setUrlPatterns(metricsProperties.getUrlPatterns());
        }
        return registrationBean;
    }
    
    @Bean
    public MetricRegistry metricRegistry()
    {
        MetricRegistry metricRegistry = new MetricRegistry();
        metricRegistry.register(PROP_METRIC_REG_JVM_MEMORY, new MemoryUsageGaugeSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_THREADS, new ThreadStatesGaugeSet());
        metricRegistry.register(PROP_METRIC_REG_JVM_GARBAGE, new GarbageCollectorMetricSet());
        return metricRegistry;
    }
    
    @Bean
    Influxdb influxdb(MetricsProperties metricsProperties)
    {
        InfluxdbConfig influxdbConfig = metricsProperties.getInfluxdb();
        return InfluxdbHttp.newBuilder()
            .dbName(influxdbConfig.getDbName())
            .host(influxdbConfig.getHost())
            .port(influxdbConfig.getPort())
            .username(influxdbConfig.getUserName())
            .password(influxdbConfig.getPassword())
            .maxSize(metricsProperties.getMaxSize())
            .build();
    }
    
    @Bean(destroyMethod = "close")
    @ConditionalOnBean(Influxdb.class)
    InfluxdbReporter influxdbReporter(MetricRegistry metricRegistry, Influxdb influxdb,
        MetricsProperties metricsProperties)
    {
        InfluxdbReporter reporter = InfluxdbReporter.forRegistry(metricRegistry)
            .convertRatesTo(TimeUnit.SECONDS)
            .convertDurationsTo(TimeUnit.MILLISECONDS)
            .appName(metricsProperties.getAppName())
            .filter(MetricFilter.ALL)
            .build(influxdb);
        reporter.start(metricsProperties.getInterval(), TimeUnit.SECONDS);
        
        return reporter;
    }
    
}