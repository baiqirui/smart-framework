package com.szzt.smart.framework.web.model;

import com.szzt.smart.framework.constant.ResultCodeConstant;
import com.szzt.smart.framework.web.config.ResultCodeConfig;

import java.io.Serializable;

/**
 * 通用返回结果
 * 
 * @author baiqirui
 * @version [版本号, 2017年8月3日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class ResultBody<T> implements Serializable
{
    private static final long serialVersionUID = 1L;
    
    // 信息码
    private int code;
    
    // 信息提示
    private String message;
    
    // 日志信息
    private String log;
    
    // 返回结果集
    private T data;
    
    public ResultBody()
    {
        super();
    }
    
    public static ResultBody fail(int code, String message)
    {
        return new ResultBody(code, message);
    }
    
    public static ResultBody fail(int code)
    {
        return new ResultBody(code, ResultCodeConfig.getResultMessage(code));
    }
    
    public static ResultBody success()
    {
        return new ResultBody(ResultCodeConstant.SUCCESS,
            ResultCodeConfig.getResultMessage(ResultCodeConstant.SUCCESS));
    }
    
    public static <T> ResultBody<T> success(T data)
    {
        return new ResultBody(ResultCodeConstant.SUCCESS, ResultCodeConfig.getResultMessage(ResultCodeConstant.SUCCESS),
            data);
    }
    
    public ResultBody(T data)
    {
        this(ResultCodeConstant.SUCCESS, ResultCodeConfig.getResultMessage(ResultCodeConstant.SUCCESS), data);
    }
    
    public ResultBody(int code, String message)
    {
        super();
        this.code = code;
        this.message = message;
    }
    
    public ResultBody(int code, String message, T data)
    {
        super();
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    public int getCode()
    {
        return code;
    }
    
    public void setCode(int code)
    {
        this.code = code;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }
    
    public T getData()
    {
        return data;
    }
    
    public void setData(T data)
    {
        this.data = data;
    }
    
    public static ResultBody createUnKnowExceptionResultBody()
    {
        return new ResultBody(ResultCodeConstant.UNKONW_EXCEPTION,
            ResultCodeConfig.getResultMessage(ResultCodeConstant.UNKONW_EXCEPTION));
    }
    
    public String getLog()
    {
        return log;
    }
    
    public void setLog(String log)
    {
        this.log = log;
    }
}
