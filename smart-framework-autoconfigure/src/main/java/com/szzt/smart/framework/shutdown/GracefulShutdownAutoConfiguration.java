package com.szzt.smart.framework.shutdown;

import javax.servlet.Servlet;

import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "shutdown.graceful.enabled", matchIfMissing = true)
@EnableConfigurationProperties(GracefulShutdownProperties.class)
@ConditionalOnBean(TomcatEmbeddedServletContainerFactory.class)
@ConditionalOnClass({Servlet.class, Tomcat.class})
public class GracefulShutdownAutoConfiguration
{
    
    @Bean
    public GracefulTomcatShutdown gracefulTomcatShutdown()
    {
        return new GracefulTomcatShutdown(gracefulShutdownProperties);
    }
    
    private GracefulShutdownProperties gracefulShutdownProperties;
    
    public GracefulShutdownAutoConfiguration(GracefulShutdownProperties gracefulShutdownProperties)
    {
        this.gracefulShutdownProperties = gracefulShutdownProperties;
    }
    
    @Bean
    public EmbeddedServletContainerCustomizer tomcatCustomizer()
    {
        return container -> {
            if (container instanceof TomcatEmbeddedServletContainerFactory)
            {
                ((TomcatEmbeddedServletContainerFactory)container).addConnectorCustomizers(gracefulTomcatShutdown());
            }
        };
    }
    
}
