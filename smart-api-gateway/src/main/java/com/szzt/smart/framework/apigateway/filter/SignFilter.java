package com.szzt.smart.framework.apigateway.filter;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.szzt.smart.framework.apigateway.config.JwtFilterConfig;
import com.szzt.smart.framework.apigateway.config.SignFilterConfig;
import com.szzt.smart.framework.apigateway.jwt.UserContext;
import com.szzt.smart.framework.util.encrypt.MD5RandomSaltEncrypt;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpStatus;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.szzt.smart.framework.apigateway.constants.ApiGatewayConstant;
import com.szzt.smart.framework.constant.ResultCodeConstant;
import com.szzt.smart.framework.web.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 接口签名过滤器
 */
@Slf4j
public class SignFilter extends ZuulFilter
{
    
    private ObjectMapper mapper = new ObjectMapper();
    
    private static final long DEFAULT_RANGE_MILLISECONDS = 5 * 60 * 1000;
    
    @Autowired
    private SignFilterConfig signFilterConfig;
    
    @Override
    public String filterType()
    {
        return "pre";
    }
    
    @Override
    public int filterOrder()
    {
        return 1;
    }
    
    @Override
    public boolean shouldFilter()
    {
        if (!signFilterConfig.getEnable())
        {
//            log.info("签名验证已关闭!");
            return false;
        }
        
        List<String> excludeds = signFilterConfig.getExcludeds();
        if (CollectionUtils.isNotEmpty(excludeds))
        {
            String url = RequestContext.getCurrentContext().getRequest().getRequestURI();
            if (excludeds.contains(url))
            {
//                log.info(url + " 接口跳过签名验证");
                return false;
            }
        }
        
        return true;
    }
    
    @Override
    public Object run()
    {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        
        // 1. 获取请求头;
        String token = request.getHeader("token");
        String timestamp = request.getHeader("timestamp");
        String sign = request.getHeader("sign");
        
        // 2.请求头参数校验
        validHeader(request);
        
        // 3.请求的实效性校验,默认实效为5分钟
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - Long.valueOf(timestamp) > DEFAULT_RANGE_MILLISECONDS)
        {
//            throw new BusinessException(ApiGatewayConstant.SIGN_TIMEEXPIRY_ERROR);
        }
        // 4.签名校验
        if (!validSign(token, timestamp, sign, request))
        {
            throw new BusinessException(ApiGatewayConstant.SIGN_VALID_ERROR);
        }
        return null;
        
    }
    
    private void validHeader(HttpServletRequest request)
    {
        // 1. 获取请求头;
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token) )
        {
            throw new BusinessException(ResultCodeConstant.PARAMETER_IS_NULL_OR_EMPTY, new String[]{"token"});
        }
        String timestamp = request.getHeader("timestamp");
        if (StringUtils.isBlank(timestamp))
        {
            throw new BusinessException(ResultCodeConstant.PARAMETER_IS_NULL_OR_EMPTY, new String[]{"timestamp"});
        }
        String sign = request.getHeader("sign");
        if (StringUtils.isBlank(sign))
        {
            throw new BusinessException(ResultCodeConstant.PARAMETER_IS_NULL_OR_EMPTY, new String[]{"sign"});
        }
    }
    
    /**
     * 签名校验
     *
     * @param token
     * @param timestamp
     * @param sign
     * @param request
     * @return
     * @throws Exception
     */
    private boolean validSign(String token, String timestamp, String sign, HttpServletRequest request)
    {
        try
        {
            // 获取参数
            Map<String, Object> paramMap = getParameter(request);
            // 对参数名进行字典排序
            String[] keyArray = paramMap.keySet().toArray(new String[0]);
            Arrays.sort(keyArray);
            
            // 参数拼接
            StringBuilder signPlaintext = new StringBuilder();
            for (int i = 0; i < keyArray.length; i++)
            {
                String key = keyArray[i];
                Object value = paramMap.get(key);
                // 如果值为null或空，或者是Map(Map嵌套表示json串复杂对象的嵌套), 则直接舍弃不做签名
                if (null == value || "".equals(value) || value instanceof Map)
                {
                    continue;
                }

                //参数拼接(参数名1=参数值1&参数名2=参数值2 ....以此类推)
                signPlaintext.append(key).append(ApiGatewayConstant.EQUAL).append(value).append(ApiGatewayConstant.AND);
            }
            

            // token,timestamp,url拼接
            signPlaintext.append(ApiGatewayConstant.TOKEN_STR)
                .append(ApiGatewayConstant.EQUAL)
                .append(token)
                .append(ApiGatewayConstant.AND)
                .append(ApiGatewayConstant.TIMESTAMP_STR)
                .append(ApiGatewayConstant.EQUAL)
                .append(timestamp)
                .append(ApiGatewayConstant.AND)
                .append(ApiGatewayConstant.URL_STR)
                .append(ApiGatewayConstant.EQUAL)
                .append(request.getRequestURI());
            // MD5参数校验
            return MD5RandomSaltEncrypt.validPassword(sign, signPlaintext.toString());
        }
        catch (Exception e)
        {
            log.error("获取签名参数出错或校验时发生错", e);
        }
        
        return false;
    }
    
    /**
     * 获取请求参数
     * 
     * @param request
     * @return
     */
    private Map<String, Object> getParameter(HttpServletRequest request)
        throws IOException
    {
        Map<String, Object> paramMap = new HashMap<>();
        
        // 获取URL上的参数
        Enumeration<String> paramNames = request.getParameterNames();
        if (null != paramNames)
        {
            while (paramNames.hasMoreElements())
            {
                String name = paramNames.nextElement();
                String value = request.getParameter(name);
                if (StringUtils.isNotBlank(value))
                {
                    paramMap.put(name, value);
                }
            }
        }
        
        // 获取body中的参数
        String bodyParam = StreamUtils.copyToString(request.getInputStream(), Charset.forName("UTF-8"));
        if (StringUtils.isNotBlank(bodyParam))
        {
            Map<String, Object> bodyMap = mapper.readValue(bodyParam, Map.class);
            paramMap.putAll(bodyMap);
        }
        
        return paramMap;
    }
    
    public static void main(String[] args)
        throws Exception
    {
        System.out.println(System.currentTimeMillis());
        System.out.println(MD5RandomSaltEncrypt.getEncryptedPwd("a=1&b=2&userId=666666&userName=Bob&token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJ1c2VybmFtZSI6IkJvYiIsImF1ZCI6IkFuZHJvaWQgQXBwIiwiZXhwIjoxNTIzMzI5Njc0LCJpc3MiOiJjbG91ZCIsImlhdCI6MTUyMzMyOTYxNCwic3ViIjoiU21hcnQifQ.h1uCjwvAwHav_rpo-KzoDgDuX2QNG6k4I5pgztqlAjcZAjhfjL1S5TxnAbN3yTdh6HDwo4gRuKdCrqawqmPIYQ&timestamp=1523329954898&url=/sample/test/addUser"));
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonString = "{\"bname\":\"Mahesh\", \"age\":21, \"test\":null}";
//        UserContext u = new UserContext();
//        u.setUserId("a");
//        u.setUserName("");
//        JwtFilterConfig config = new JwtFilterConfig();
//        config.setEnable(true);
//        u.setConfig(config);
//        String json = mapper.writeValueAsString(u);
//        System.out.println(json);
//        Map m = mapper.readValue(json, Map.class);
//        System.out.println(m);
//        System.out.println(m.get("config").getClass());
//        System.out.println("".equals(m.get("properties")));
//        System.out.println(null == m.get("properties"));
    }
    
}