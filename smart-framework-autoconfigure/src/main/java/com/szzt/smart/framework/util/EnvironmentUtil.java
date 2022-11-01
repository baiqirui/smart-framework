package com.szzt.smart.framework.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.ConfigurableEnvironment;

@Slf4j
public class EnvironmentUtil
{
    // 读取配置并转换成对象
    public static <T> T resolverSetting(Class<T> propertiesClass, String targetName, ConfigurableEnvironment environment,
        String propertiesName)
    {
        Binder binder = Binder.get(environment);
        return binder.bind("webull.lettuce", Bindable.of(propertiesClass)).get();
//
//        ConfigurationFactory.PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<>(clazz);
//        factory.setTargetName(targetName);
//        factory.setPropertySources(environment.getPropertySources());
//        factory.setConversionService(environment.getConversionService());
//        try
//        {
//            factory.bindPropertiesToTarget();
//        }
//        catch (BindException e)
//        {
//            // ignore bind exception
//        }
//        try
//        {
//            return (T)factory.getObject();
//        }
//        catch (Exception e)
//        {
//            log.error("Could not bind" + propertiesName + "properties: " + e.getMessage(), e);
//            throw new FatalBeanException("Could not bind" + propertiesName + " properties", e);
//        }
    }

}
