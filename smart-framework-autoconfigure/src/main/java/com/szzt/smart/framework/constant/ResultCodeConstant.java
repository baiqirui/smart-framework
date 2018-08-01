package com.szzt.smart.framework.constant;

/**
 * 框架自定义错误码.100001 - 100999
 */
public class ResultCodeConstant
{
    public static final int UNKONW_EXCEPTION = -1;
    
    public static final int SUCCESS = 0;
    
    public static final int PARAMETER_IS_NULL_OR_EMPTY = 100001;
    
    public static final int PARAMETER_IS_OUT_OF_RANGE = 100002;
    
    public static final int PARAMETER_IS_NOT_BETWEEN_IN = 100003;
    
    public static final int PARAMETER_IS_NOT_ENUM_IN = 100004;
    
    public static final int PARAMETER_INVALID = 100005;
    
    public static final int HTTP_METHOD_NOT_ALLOW_ERROR = 100006;
    
    public static final int HTTP_CONNECTION_TIME_OUT = 100007;
    
    public static final int HTTP_READ_TIME_OUT = 100008;
    
    public static final int HTTP_INNTER_CONNECTION_TIME_OUT = 100009;
    
    public static final int HTTP_INNTER_READ_TIME_OUT = 100010;
    
    public static final int HTTP_INNTER_SERVICE_NOT_AVAILABLE = 100011;
    
    public static final int HTTP_INNTER_ERROR = 100012;
    
    public static final int HTTP_ERROR = 100013;

    /**
     * 记录已经存在,请勿重复添加
     */
    public static final int RECORD_REPEAT_ERROR = 100014;

}
