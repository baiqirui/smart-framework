package com.szzt.smart.framework.web.util.transfer.impl;


//package com.mealkey.core.util.transfer.impl;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

import com.szzt.smart.framework.web.util.transfer.ValueObject;
import com.szzt.smart.framework.web.util.transfer.ValueObjectUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * ValueObjectBean为ValueObject的一个标准实现,可以将目标对象适配成ValueObjectBean来进行统一处理;
 * 
 * @author baiqirui
 * @version [版本号, 2012-5-4]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Slf4j
public class ValueObjectBean implements ValueObject
{
    private static final long serialVersionUID = 6425750269754458356L;
    
    /** 存放原始bean */
    private Object obj;
    
    /** 存放原始bean的所有set方法 */
    private Map<String, Method> setMethodMap = new HashMap<String, Method>();
    
    /** 存放原始bean的所有get方法 */
    private Map<String, Method> getMethodMap = new HashMap<String, Method>();
    
    /** 存放扩展属性 */
    private Map<String, Object> extendMap = new HashMap<String, Object>();
    
    /** 存放原始bean的所有属性名称 */
    private List<String> attrNames = new ArrayList<String>();
    
    private PropertyDescriptor[] pros;
    
    @SuppressWarnings("unchecked")
    public ValueObjectBean(Object obj)
    {
        if (null != obj)
        {
            if (obj instanceof Map)
            {
                this.extendMap = (Map<String, Object>) obj;
            }
            else
            {
                this.obj = obj;
                init();
            }
        }
    }
    
    /**
     * 初始化目标对象的属性名和内省方法;
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    private void init()
    {
        try
        {
            // 获得PropertyDescriptor对象：描述受此 bean 支持的可编辑属性的 PropertyDescriptor 数组
            pros = Introspector.getBeanInfo(this.obj.getClass()).getPropertyDescriptors();
            for (int i = 0; i < pros.length; i++)
            {
                attrNames.add(pros[i].getName());
            }
            initGetMethod();
            initSetMethod();
        }
        catch (IntrospectionException e)
        {
            e.printStackTrace();
        }
        
    }
    
    /**
     * 初始化bean的setXXX方法;
     */
    private void initSetMethod()
    {
        for (int i = 0; i < pros.length; i++)
        {
            Method writeMethod = pros[i].getWriteMethod();
            if (null != writeMethod)
            {
                String key = "set." + pros[i].getName();
                setMethodMap.put(key, writeMethod);
            }
        }
    }
    
    /**
     * 初始化bean的getXXX方法;
     */
    private void initGetMethod()
    {
        for (int i = 0; i < pros.length; i++)
        {
            Method getReadMethod = pros[i].getReadMethod();
            String key = "get." + pros[i].getName();
            getMethodMap.put(key, getReadMethod);
        }
    }
    
    /**
     * 获取该对象的所有属性名称;
     * 
     * @return [参数说明]
     * 
     * @return List<String> [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public List<String> getAttrNames()
    {
        return attrNames;
    }
    
    /**
     * 得到适配之前的原始Bean;
     * 
     * @return [参数说明]
     * 
     * @return Object [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public Object getBean()
    {
        return this.obj;
    }
    
    /**
     * 根据bean的属性名获取值,支持多层获取;
     * 
     * @param attrName
     * @return
     * @throws Exception
     */
    public Object getValue(String attrName)
        throws Exception
    {
        Object value = null;
        String[] attrNames = attrName.split("\\.");
        String key = "get." + attrNames[0];
        Method getMethod = getMethodMap.get(key);
        if (null != getMethod)
        {
            try
            {
                value = getMethod.invoke(this.obj);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            // 判断是否是多层获取值对象;
            if (attrNames.length > 1)
            {
                for (int i = 1; i < attrNames.length; i++)
                {
                    String methodName = "get" + attrNames[i].substring(0, 1).toUpperCase() + attrNames[i].substring(1);
                    Method method = value.getClass().getDeclaredMethod(methodName);
                    value = method.invoke(value);
                }
            }
        }
        else
        {
            // 从扩展信息中获取属性值;
            value = extendMap.get(attrName);
        }
        return value;
    }
    
    /**
     * <一句话功能简述> <功能详细描述>
     * 
     * @param attrName
     * @param value [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public void putValue(String attrName, Object value)
        throws Exception
    {
        if (null == value)
        {
            return;
        }
        try
        {
            String key = "set." + attrName;
            // 如果是为原始bean的本身属性赋值,则直接采用原始bean的set方法保存,否则保存到扩展属性值;
            if (setMethodMap.containsKey(key))
            {
                Method setMethod = setMethodMap.get(key);
                Class<?> valueClassType = value.getClass();
                // 获取set方法中的参数类型
                Class<?>[] srcClasses = setMethod.getParameterTypes();
                // 如果未知异常原因导致set方法没有参数,则不赋值;
                if (null == srcClasses || srcClasses.length < 1)
                {
                    return;
                }
                Class<?> srcClassType = srcClasses[0];
                // 如果原始参数类型与目标参数类型一致 或者 原始参数时目标参数的父类，这2种情况则直接赋值;
                if (valueClassType.equals(srcClassType) || (ValueObjectUtil.isSuperClass(srcClassType, valueClassType)))
                {
                    setMethod.invoke(this.obj, value);
                }
                else
                {
                    if (ValueObjectUtil.isSuperClass(Date.class, srcClassType))
                    {
                        setMethod.invoke(this.obj, ValueObjectUtil.transferToDate(value, srcClassType));
                    }
                    else
                    {
                        ValueObjectUtil.setValue(setMethod, srcClassType, this.obj, value);
                    }
                }
            }
            else
            {
                extendMap.put(attrName, value);
            }
        }
        catch (Exception e)
        {
            log.error("in putValue method occur error");
            throw e;
        }
        
    }
    
}
