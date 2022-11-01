package com.szzt.smart.framework.web.util.transfer;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * ValueObject的工具类,为其提供公共处理 <功能详细描述>
 * 
 * @author baiqirui
 * @version [版本号, 2012-5-4]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Slf4j
public final class ValueObjectUtil
{
    private static final String DATE_MODEL1 = "yyyyMMdd";
    
    private static final String DATE_MODEL2 = "yyyy-MM-dd";
    
    private static final String DATE_MODEL3 = "yyyyMMddHHmmss";
    
    private static final String DATE_MODEL4 = "yyyy-MM-dd HH:mm:ss";
    
    private ValueObjectUtil()
    {
        
    }
    
    /**
     * 两对象直接的值拷贝,暂时不支持数组和集合的赋值;
     * 
     * @param srcObject [源对象]
     * @param destObject [需要赋值的目标对象的ClassType]
     * @return [需要赋值的目标对象]
     * @throws Exception [参数说明]
     * 
     * @return Object [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("unchecked")
    public static <T> T transfer(Object srcObject, Class<T> destClass)
    {
          try
          {
              Object destObj = transfer(srcObject, destClass.newInstance());
               return (T)destObj;
          } 
          catch (Exception e)
          {
                e.printStackTrace();
          } 
          return null;
    }
    
    
    /**
     * 两对象直接的值拷贝,暂时不支持数组和集合的赋值;
     * 
     * @param srcObject [源对象]
     * @param destObject [需要赋值的目标对象]
     * @return [需要赋值的目标对象]
     * @throws Exception [参数说明]
     * 
     * @return Object [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static Object transfer(Object srcObject, Object destObject)
    {
        try
        {
            if (null == srcObject || null == destObject)
            {
                return null;
            }
            // 将两个对象适配成公共的ValueObject,方便扩展和赋值;
            ValueObject srcValueObject = ValueObjectFactory.newInstance(srcObject);
            ValueObject destValueObject = ValueObjectFactory.newInstance(destObject);
            
            List<String> attrNames = srcValueObject.getAttrNames();
            for (int i = 0; i < attrNames.size(); i++)
            {
                String attrName = attrNames.get(i);
                destValueObject.putValue(attrName, srcValueObject.getValue(attrName));
            }
            return destValueObject.getBean();
        }
        catch (Exception e)
        {
            log.error("值对象拷贝时发生错误,在" + ValueObjectUtil.class.getName() + "的 transfer方法中.", e);
        }
        return destObject;
    }
    
    /**
     * 根据不同的类型进行设置值;
     * 
     * @param setMethod [set方法对象]
     * @param classType [set方法中的参数类型]
     * @param obj [需要进行赋值的目标对象]
     * @param value [实际值]
     * @throws Exception
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void setValue(Method setMethod, Class<?> classType, Object obj, Object value)
        throws Exception
    {
        try
        {
            String strValue = transferToString(value);
            if (classType.equals(String.class))
            {
                setMethod.invoke(obj, strValue);
            }
            else if (classType.equals(Integer.class))
            {
                setMethod.invoke(obj, Integer.parseInt(strValue));
            }
            else if (classType.equals(int.class))
            {
                setMethod.invoke(obj, Integer.parseInt(strValue));
            }
            else if (classType.equals(Integer.class))
            {
                setMethod.invoke(obj, new Integer(strValue));
            }
            else if (classType.equals(float.class))
            {
                setMethod.invoke(obj, Float.parseFloat(strValue));
            }
            else if (classType.equals(Float.class))
            {
                setMethod.invoke(obj, new Float(strValue));
            }
            else if (classType.equals(double.class))
            {
                setMethod.invoke(obj, Double.parseDouble(strValue));
            }
            else if (classType.equals(Double.class))
            {
                setMethod.invoke(obj, new Double(strValue));
            }
            else if (classType.equals(long.class))
            {
                setMethod.invoke(obj, Long.parseLong(strValue));
            }
            else if (classType.equals(Long.class))
            {
                setMethod.invoke(obj, new Long(strValue));
            }
            else if (classType.equals(short.class))
            {
                setMethod.invoke(obj, Short.parseShort(strValue));
            }
            else if (classType.equals(Short.class))
            {
                setMethod.invoke(obj, new Short(strValue));
            }
            else if (classType.equals(byte.class))
            {
                setMethod.invoke(obj, Byte.parseByte(strValue));
            }
            else if (classType.equals(Byte.class))
            {
                setMethod.invoke(obj, new Byte(strValue));
            }
            else if (classType.equals(boolean.class))
            {
                setMethod.invoke(obj, Boolean.parseBoolean(strValue));
            }
            else if (classType.equals(Boolean.class))
            {
                setMethod.invoke(obj, new Boolean(strValue));
            }
            else if (classType.equals(char.class))
            {
                setMethod.invoke(obj, strValue.charAt(0));
            }
            else if (classType.equals(Character.class))
            {
                setMethod.invoke(obj, new Character(strValue.charAt(0)));
            }
            else if (classType.equals(BigDecimal.class))
            {
                setMethod.invoke(obj, new BigDecimal(strValue));
            }
            else if (classType.equals(BigInteger.class))
            {
                setMethod.invoke(obj, new BigInteger(strValue));
            }
            
        }
        catch (Exception e)
        {
            log.error("对象设值时发生错误,在" + ValueObjectUtil.class.getName() + "的 setValue方法中.");
            throw e;
        }
    }
    
    /**
     * 将值转换成String类型;
     * 
     * @param value
     * @return [参数说明]
     * 
     * @return String [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static String transferToString(Object value)
    {
        String strValue = null;
        if (null == value)
        {
            return strValue;
        }
        if (value instanceof Date)
        {
            DateFormat f = new SimpleDateFormat(DATE_MODEL4);
            strValue = f.format(value);
            return strValue;
        }
        if (value instanceof String)
        {
            return (String)value;
        }
        return value.toString();
    }
    
    /**
     * 将值转换成Date类型;
     * 
     * @param srcValue
     * @param destValueType
     * @return [参数说明]
     * 
     * @return Date [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static Date transferToDate(Object srcValue, Class<?> destValueType)
        throws Exception
    {
        Date date = null;
        String strValue = transferToString(srcValue);
        int length = strValue.length();
        DateFormat df = null;
        // 判断日期的格式
        if (length == 8)
        {
            df = new SimpleDateFormat(DATE_MODEL1);
        }
        else if (length == 14)
        {
            df = new SimpleDateFormat(DATE_MODEL3);
        }
        else if (strValue.indexOf('-') > 1 && strValue.indexOf(':') > 1)
        {
            df = new SimpleDateFormat(DATE_MODEL4);
        }
        else
        {
            df = new SimpleDateFormat(DATE_MODEL2);
        }
        try
        {
            date = df.parse(strValue);
        }
        catch (ParseException e)
        {
            log.error("日期时发生错误在" + ValueObjectUtil.class.getName() + "的 transferToDate方法中.");
            throw e;
        }
        // 判断Date的真实类型
        if (destValueType.equals(Timestamp.class))
        {
            Timestamp t = new Timestamp(0);
            t.setTime(date.getTime());
            return t;
        }
        else if (destValueType.equals(java.sql.Date.class))
        {
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            return sqlDate;
        }
        return date;
        
    }
    
    /**
     * String的空值判断;
     * 
     * @param value
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isEmptyString(String value)
    {
        return null == value || "".equals(value.trim());
    }
    
    /**
     * 判断是否是集合
     * 
     * @param value
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isCollection(Object value)
    {
        if (null == value)
        {
            return false;
        }
        if (value instanceof Map)
        {
            return true;
        }
        return isSuperClass(Collection.class, value.getClass());
    }
    
    /**
     * 判定此Class对象所表示的类或接口与指定的 Class 参数所表示的类或接口是否相同，或是否是其父类或父接口。
     * 
     * @param superClass
     * @param subClass
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isSuperClass(Class<?> superClass, Class<?> subClass)
    {
        return superClass.isAssignableFrom(subClass);
    }
    
    /**
     * 检查入参是否为null或空字符串(若是则抛出异常)
     * 
     * @param obj
     * @param paramList
     * @throws ServiceException [参数说明]
     * 
     * @return void [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static void checkParam(Object obj, List<String> paramList)
        throws Exception
    {
        // 如果检验对象本身为空,则抛出异常告知;
        if (null == obj)
        {
            throw new Exception("value Object is null");
        }
        // 如果需要校验的属性集合为空,则直接返回,不校验;
        if (isEmptyCollection(paramList))
        {
            return;
        }
        // 适配成统一的ValueObject;
        ValueObject valueObj = (obj instanceof ValueObject) ? (ValueObject)obj : ValueObjectFactory.newInstance(obj);
        for (int i = 0; i < paramList.size(); i++)
        {
            String attrName = paramList.get(i);
            Object value = null;
            try
            {
                value = valueObj.getValue(attrName);
            }
            catch (Exception e)
            {
                throw new Exception("系统内部异常");
            }
            if (isNullOrEmpty(value))
            {
                throw new Exception(attrName + " is null or empty");
            }
        }
    }
    
    /**
     * 判断集合是否为空
     * 
     * @param c
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isEmptyCollection(Collection<?> c)
    {
        return (null == c || c.isEmpty());
    }
    
    /**
     * 判断集合是否不为空 <功能详细描述>
     * 
     * @param c
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNotEmptyCollection(Collection<?> c)
    {
        return !isEmptyCollection(c);
    }
    
    /**
     * 判断集合是否为空
     * 
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isEmptyMap(Map<?, ?> map)
    {
        return (null == map || map.isEmpty());
    }
    
    /**
     * 判断集合是否不为空 <功能详细描述>
     * 
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNotEmptyMap(Map<?, ?> map)
    {
        return !isEmptyMap(map);
    }
    
    /**
     * 判断对象是否为null或者为空
     * 
     * @param value
     * @return [参数说明]
     * 
     * @return boolean [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static boolean isNullOrEmpty(Object value)
    {
        if (null == value)
        {
            return true;
        }
        if (value instanceof String)
        {
            return isEmptyString((String)value);
        }
        if (value instanceof Map)
        {
            return isEmptyMap((Map<?, ?>)value);
        }
        if (value instanceof Collection<?>)
        {
            return isEmptyCollection((Collection<?>)value);
        }
        return false;
    }
    
    /**
     * 比较两个对象不一样的属性值;
     *
     * @param srcObject
     * @param destObject[参数、异常说明]
     * @return void [返回类型说明]
     * @throws Exception 
     * @see [类、类#方法、类#成员]
     */
    public static void  compareObject(Object srcObject, Object destObject) throws Exception
    {
        if (null == srcObject || null == destObject)
        {
            return;
        }
        
        // 将两个对象适配成公共的ValueObject,方便扩展和赋值;
        ValueObject srcValueObject = ValueObjectFactory.newInstance(srcObject);
        ValueObject destValueObject = ValueObjectFactory.newInstance(destObject);
        
        List<String> attrNames = srcValueObject.getAttrNames();
        
        for (String attrName : attrNames)
        {
            Object srcValue = srcValueObject.getValue(attrName);
            Object destValue = destValueObject.getValue(attrName);
            if (srcValue == destValue)
            {
                continue;
            }
            if (null != srcValue ? srcValue.equals(destValue) : destValue.equals(srcValue))
            {
                continue;
            }
            System.out.println("src:" + attrName + " === " + srcValueObject.getValue(attrName) + " ------ " + "dest:" + attrName + " === " + destValueObject.getValue(attrName));
        }
    }
}