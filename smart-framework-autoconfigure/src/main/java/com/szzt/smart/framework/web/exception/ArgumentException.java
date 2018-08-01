package com.szzt.smart.framework.web.exception;

import com.szzt.smart.framework.constant.ResultCodeConstant;
import org.apache.commons.lang.enums.EnumUtils;


/**
 * 参数异常
 * 
 * @author baiqirui
 * @version [版本号, 2017年8月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ArgumentException extends ExceptionBase
{
    private static final long serialVersionUID = 1L;
    
    
    public ArgumentException(int errorCode, String... arguments)
    {
        super(errorCode, arguments);
    }
    

    /**
     * 检测参数值是否为空引用、空字符串或者只包含空白字符的字符串。
     * 
     * @param argumentName 参数的名称。
     * @param argumentValue 参数的实际值。
     */
    public static void checkNullOrBlankString(String argumentName, String argumentValue)
    {
        if ((null == argumentValue) || (0 == argumentValue.length()) || (0 == argumentValue.trim().length()))
        {
            throw createNullOrBlankString(argumentName);
        }
    }
    
    /**
     * 创建 ArgumentException 异常，并指定导致异常的原因为 “值不能是空引用、空字符串或者只包含空白字符的字符串”。
     * 
     * @param argumentName 参数的名称。
     */
    public static ArgumentException createNullOrBlankString(String argumentName)
    {
        ArgumentException ex = new ArgumentException(ResultCodeConstant.PARAMETER_IS_NULL_OR_EMPTY, argumentName);
        return ex;
    }
    
    /**
     * 创建 ArgumentException 异常，并指定导致异常的原因为 参数过长
     * 
     * @param argumentName 参数的名称。
     */
    public static ArgumentException createOutOfRange(String argumentName)
    {
        ArgumentException ex = new ArgumentException(ResultCodeConstant.PARAMETER_IS_OUT_OF_RANGE, argumentName);
        return ex;
    }
    
    /**
     * 创建 ArgumentException 异常，并指定导致异常的原因为 不在指定值范围内
     * 
     * @param argumentNames 参数的名称。
     */
    public static ArgumentException createNotBetweenIn(String... argumentNames)
    {
        ArgumentException ex = new ArgumentException(ResultCodeConstant.PARAMETER_IS_NOT_BETWEEN_IN, argumentNames);
        return ex;
    }
    
    /**
     * 检测参数值是否为在指定值的范围内
     *
     * @param argumentName
     * @param argumentValue
     * @param minValue
     * @param maxValue
     * @return void [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static void checkNotBetweenIn(String argumentName, int argumentValue, int minValue, int maxValue)
    {
        if (argumentValue < minValue || minValue > maxValue)
        {
            String[] argumentNames = {argumentName, String.valueOf(minValue), String.valueOf(maxValue)};
            throw createNotBetweenIn(argumentNames);
        }
    }
    
    /**
     * 创建 ArgumentException 异常，并指定导致异常的原因为 不在枚举值范围内
     * 
     * @param argumentNames 参数的名称。
     */
    public static ArgumentException createNotEnumIn(String... argumentNames)
    {
        ArgumentException ex = new ArgumentException(ResultCodeConstant.PARAMETER_IS_NOT_ENUM_IN, argumentNames);
        return ex;
    }
    
    /**
     * 检测参数值是否为在指定值的范围内
     *
     * @param argumentName
     * @param argumentValue
     * @param enumClass
     * @return void [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static void checkNotEnumIn(String argumentName, int argumentValue, Class<?> enumClass)
    {
        if (null == EnumUtils.getEnum(enumClass, argumentValue))
        {
            String[] argumentNames = {argumentName, String.valueOf(argumentValue)};
            throw createNotEnumIn(argumentNames);
        }
    }
}
