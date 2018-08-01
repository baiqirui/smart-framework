package com.szzt.smart.framework.web.exception;

/**
 * 业务异常;
 * 
 * @author  baiqirui
 * @version  [版本号, 2017年8月3日]
 * @see  [相关类/方法]
 * @since  [产品/模块版本]
 */
public class BusinessException extends ExceptionBase
{
    private static final long serialVersionUID = 1L;

    public BusinessException(int errorCode)
    {
        super(errorCode);
    }
    
    public BusinessException(int errorCode, String message)
    {
        super(errorCode, message);
    }
    
    public BusinessException(int errorCode, String... arguments)
    {
        super(errorCode, arguments);
    }
}
