package com.szzt.smart.framework.apigateway;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.DefaultErrorAttributes;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.context.request.RequestAttributes;

import com.netflix.zuul.exception.ZuulException;
import com.szzt.smart.framework.apigateway.constants.ApiGatewayConstant;
import com.szzt.smart.framework.apigateway.filter.JwtTokenFilter;
import com.szzt.smart.framework.apigateway.filter.SignFilter;
import com.szzt.smart.framework.web.config.ResultCodeConfig;
import com.szzt.smart.framework.web.exception.ExceptionBase;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@EnableDiscoveryClient // 开启服务发现
@EnableCircuitBreaker // 开启熔断器
@EnableFeignClients // 开启声明式的web service客户端
@EnableZuulProxy
@ComponentScan(basePackages = {"com.szzt.smart"})
public class SmartApiGatewayApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(SmartApiGatewayApplication.class, args);
    }
    

    /**
     * jwtToken 过滤器
     * @return
     */
    @Bean
    public JwtTokenFilter tokenFilter()
    {
        return new JwtTokenFilter();
    }

    /**
     * 签名过滤器
     * @return
     */
    @Bean
    public SignFilter signFilter()
    {
        return new SignFilter();
    }


    /**
     * 灰度发布过滤器
     * @return
     */
//    @Bean
//    public PreFilter preFilter()
//    {
//        return new PreFilter();
//    }

    /**
     * 重写网关的异常属性，输出系统自定义的公异常属性
     * 
     * @return
     */
    @Bean
    public DefaultErrorAttributes errorAttributes()
    {
        return new DefaultErrorAttributes()
        {
            @Override
            public Map<String, Object> getErrorAttributes(RequestAttributes requestAttributes,
                boolean includeStackTrace)
            {
                ZuulException exception = (ZuulException)requestAttributes.getAttribute("javax.servlet.error.exception",
                    RequestAttributes.SCOPE_REQUEST);
                Map<String, Object> errorAttributes = new HashMap<>();
                Throwable cause = exception.getCause();
                //如果封装的内部异常是ExceptionBase,则自动获取错误码和错误信息
                if (null != cause && cause instanceof ExceptionBase)
                {
                    ExceptionBase exb = (ExceptionBase)cause;
                    errorAttributes.put("code", exb.getErrorCode());
                    errorAttributes.put("message", exb.getMessage());
                }
                else
                {
                    errorAttributes.put("code", ApiGatewayConstant.API_GATEWAY_ERROR);
                    errorAttributes.put("message",
                        ResultCodeConfig.getResultMessage(ApiGatewayConstant.API_GATEWAY_ERROR));
                }
                return errorAttributes;
            }
        };
    }
    
}
