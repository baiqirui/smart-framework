package com.szzt.smart.framework.web.exception;

import java.text.MessageFormat;

import com.szzt.smart.framework.constant.ResultCodeConstant;
import com.szzt.smart.framework.web.config.ResultCodeConfig;
import org.apache.commons.lang.StringUtils;


/**
 * 表示在应用程序执行过程中发生的错误。
 * @author baiqirui
 */
public class ExceptionBase extends RuntimeException
{
    private static final long serialVersionUID = 1L;
    
    protected String[] arguments;
    
    protected int errorCode;
    
    protected String errorMessage;
    
    public ExceptionBase(int errorCode, String... arguments)
    {
        this.errorCode = errorCode;
        this.arguments = arguments;
        String message = ResultCodeConfig.getResultMessage(errorCode);
        if (StringUtils.isBlank(message))
        {
            this.errorMessage = ResultCodeConfig.getResultMessage(ResultCodeConstant.UNKONW_EXCEPTION);
        }
        else
        {
            this.errorMessage = MessageFormat.format(message, arguments);
        }
    }
    
    public ExceptionBase()
    {
        super();
    }
    
    public ExceptionBase(int errorCode, String errorMessage)
    {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
    }
    
    public int getErrorCode()
    {
        return errorCode;
    }

    public String[] getArguments()
    {
        return arguments;
    }
    
    @Override
    public String getMessage()
    {
        return this.errorMessage;
    }
}
