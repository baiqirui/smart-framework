package com.szzt.smart.framework.xxljob;//package com.szzt.smart.framework.swagger;

import com.xxl.job.core.executor.XxlJobExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.szzt.smart.framework.ConditionalOnMapProperty;

@Slf4j
@Configuration
@ConditionalOnClass(XxlJobExecutor.class)
@ConditionalOnMapProperty(prefix = "xxljob.")
@EnableConfigurationProperties(XxlJobProperties.class)
public class XxlJobConfiguration
{

    @Bean(initMethod = "start", destroyMethod = "destroy")
    public XxlJobExecutor xxlJobExecutor(XxlJobProperties xxlJobProperties)
    {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobExecutor xxlJobExecutor = new XxlJobExecutor();
        xxlJobExecutor.setIp(xxlJobProperties.getIp());
        xxlJobExecutor.setPort(xxlJobProperties.getPort());
        xxlJobExecutor.setAppName(xxlJobProperties.getAppname());
        xxlJobExecutor.setAdminAddresses(xxlJobProperties.getAddresses());
        xxlJobExecutor.setLogPath(xxlJobProperties.getLogpath());
        xxlJobExecutor.setAccessToken(xxlJobProperties.getAccessToken());
        return xxlJobExecutor;
    }

}