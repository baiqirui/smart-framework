package com.szzt.smart.framework.web.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SpringContextConfig implements ApplicationContextAware
{
    
    private static ApplicationContext applicationContext; // Spring应用上下文环境
    
    public void setApplicationContext(ApplicationContext applicationContext)
        throws BeansException
    {
        SpringContextConfig.applicationContext = applicationContext;
    }
    
    /**
     * @return ApplicationContext
     */
    public static ApplicationContext getApplicationContext()
    {
        return applicationContext;
    }
    
    /**
     * 获取对象
     *
     * @return Object 一个以所给名字注册的bean的实例
     */
    public static <T> T getBean(String name)
        throws BeansException
    {
        return (T)applicationContext.getBean(name);
    }
    
    /**
     * 获取类型为requiredType的对象 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）
     *
     * @param name bean注册名
     * @param requiredType 返回对象类型
     * @return Object 返回requiredType类型对象
     */
    public static <T> T getBean(String name, Class<T> requiredType)
        throws BeansException
    {
        return applicationContext.getBean(name, requiredType);
    }
    
    /**
     * 获取类型为requiredType的对象 如果bean不能被类型转换，相应的异常将会被抛出（BeanNotOfRequiredTypeException）
     *
     * @param requiredType 返回对象类型
     * @return Object 返回requiredType类型对象
     */
    public static <T> T getBean(Class<T> requiredType)
        throws BeansException
    {
        return applicationContext.getBean(requiredType);
    }
    
    /**
     * 获取类为requiredType的对象列表
     *
     * @param requiredType requiredType 返回对象类型
     */
    public static <T> String[] getBeans(Class<T> requiredType)
    {
        return applicationContext.getBeanNamesForType(requiredType);
    }
    
    /**
     * 如果BeanFactory包含一个与所给名称匹配的bean定义，则返回true
     *
     * @return boolean
     */
    public static boolean containsBean(String name)
    {
        return applicationContext.containsBean(name);
    }
    
    /**
     * 判断以给定名字注册的bean定义是一个singleton还是一个prototype。 如果与给定名字相应的bean定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     *
     * @return boolean
     */
    public static boolean isSingleton(String name)
        throws NoSuchBeanDefinitionException
    {
        return applicationContext.isSingleton(name);
    }
    
    /**
     * @return Class 注册对象的类型
     */
    public static Class getType(String name)
        throws NoSuchBeanDefinitionException
    {
        return applicationContext.getType(name);
    }
    
    /**
     * 如果给定的bean名字在bean定义中有别名，则返回这些别名
     */
    public static String[] getAliases(String name)
        throws NoSuchBeanDefinitionException
    {
        return applicationContext.getAliases(name);
    }
    
}
