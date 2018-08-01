package com.szzt.smart.framework.shutdown;

import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextClosedEvent;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GracefulTomcatShutdown
    implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent>, ApplicationContextAware
{
    
    private volatile Connector connector;
    
    @Getter
    private Date startShutdown;
    
    private Date stopShutdown;
    
    private final GracefulShutdownProperties shutdownProperties;
    
    private ConfigurableApplicationContext context;
    
    public GracefulTomcatShutdown(GracefulShutdownProperties shutdownProperties)
    {
        this.shutdownProperties = shutdownProperties;
    }
    
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext)
        throws BeansException
    {
        if (applicationContext instanceof ConfigurableApplicationContext)
        {
            this.context = (ConfigurableApplicationContext)applicationContext;
        }
    }
    
    @Override
    public void customize(Connector connector)
    {
        this.connector = connector;
    }
    
    @Override
    public void onApplicationEvent(final ContextClosedEvent event)
    {
        try
        {
            startShutdown = new Date();
            log.info("We are now in down mode, please wait " + shutdownProperties.getWait() + " second(s)...");
            Thread.sleep(shutdownProperties.getWait() * 1000);
            connector.pause();
            log.info(
                "Graceful shutdown in progess... We don't accept new connection... Wait after latest connections (max : "
                    + shutdownProperties.getTimeout() + " seconds)... ");
            
            final Executor executor = connector.getProtocolHandler().getExecutor();
            if (executor instanceof ThreadPoolExecutor)
            {
                log.info("executor is  ThreadPoolExecutor");
                final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor)executor;
                threadPoolExecutor.shutdown();
                if (!threadPoolExecutor.awaitTermination(shutdownProperties.getTimeout(), TimeUnit.SECONDS))
                {
                    log.warn("Tomcat thread pool did not shut down gracefully within " + shutdownProperties.getTimeout()
                        + " second(s). Proceeding with force shutdown");
                }
                else
                {
                    log.debug("Tomcat thread pool is empty, we stop now");
                }
            }
            stopShutdown = new Date();
        }
        catch (final InterruptedException ex)
        {
            log.error("The await termination has been interrupted : " + ex.getMessage());
            Thread.currentThread().interrupt();
        }
        finally
        {
            if (stopShutdown != null && startShutdown != null)
            {
                final long seconds = (stopShutdown.getTime() - startShutdown.getTime()) / 1000;
                log.info("Shutdown performed in " + seconds + " second(s)");
            }
        }
    }
    
}
