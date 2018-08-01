package com.szzt.smart.framework.web.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties
@PropertySource(value = {"classpath:result_code.properties",
    "classpath:${spring.application.name}_result_code.properties"}, encoding = "utf-8", ignoreResourceNotFound = true)
public class ResultCodeConfig
{
    private static Map<Integer, String> result_code = new HashMap<Integer, String>();
    
    public Map<Integer, String> getResult_code()
    {
        return result_code;
    }

    public  void setResult_code(Map<Integer, String> result_code)
    {
        ResultCodeConfig.result_code = result_code;
    }

    public static String getResultMessage(int code)
    {
        return result_code.get(code);
    }
}
