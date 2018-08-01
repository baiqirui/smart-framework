package com.szzt.smart.framework.util;

import org.springframework.beans.FatalBeanException;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.validation.BindException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnvironmentUtil
{
    // 读取配置并转换成对象
    public static <T> T resolverSetting(Class<T> clazz, String targetName, ConfigurableEnvironment environment,
        String propertiesName)
    {
        PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<>(clazz);
        factory.setTargetName(targetName);
        factory.setPropertySources(environment.getPropertySources());
        factory.setConversionService(environment.getConversionService());
        try
        {
            factory.bindPropertiesToTarget();
        }
        catch (BindException e)
        {
            // ignore bind exception
        }
        try
        {
            return (T)factory.getObject();
        }
        catch (Exception e)
        {
            log.error("Could not bind" + propertiesName + "properties: " + e.getMessage(), e);
            throw new FatalBeanException("Could not bind" + propertiesName + " properties", e);
        }
        
    }
    
}
