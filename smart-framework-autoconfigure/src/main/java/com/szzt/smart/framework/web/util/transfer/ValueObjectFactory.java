package com.szzt.smart.framework.web.util.transfer;


import com.szzt.smart.framework.web.util.transfer.impl.ValueObjectBean;

/**
 * 
 * <一句话功能简述> <功能详细描述>
 * 
 * @author lenovo
 * @version [版本号, 2012-5-4]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public final class ValueObjectFactory
{
    
    private ValueObjectFactory()
    {
        
    }
    
    /**
     * 将目标对象适配成ValueObject对象;
     * 
     * @param obj
     * @return [参数说明]
     * 
     * @return ValueObject [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static ValueObject newInstance(Object obj)
    {
        if (obj instanceof ValueObject)
        {
            return (ValueObject)obj;
        }
        return new ValueObjectBean(obj);
    }
    
}
