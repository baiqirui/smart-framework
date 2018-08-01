package com.szzt.smart.framework.web.util.transfer;

import java.io.Serializable;
import java.util.List;

/**
 * 统一值对象接口标识,每个不同的最对象都可以适配成ValueObject对象来扩展新功能;
 * 
 * @author baiqirui
 * @version [版本号, 2012-5-4]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public interface ValueObject extends Serializable
{
    /**
     * 获取适配之前的原始Bean的所有属性名称;
     * 
     * @return [参数说明]
     * 
     * @return List<String> [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public List<String> getAttrNames();
    
    /**
     * 得到适配之前的原始Bean;
     * 
     * @return [参数说明]
     * 
     * @return Object [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public Object getBean();
    
    /**
     * 根据属性名称来获取对应的值,可以多层获取[如:a.b.c];
     * 
     * @param attrName
     * @return
     * @throws Exception [参数说明]
     * 
     * @return Object [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public Object getValue(String attrName)
        throws Exception;
    
    /**
     * 为对象put的值;
     * 
     * @param attrName
     * @param value
     * @throws Exception [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public void putValue(String attrName, Object value)
        throws Exception;
}
