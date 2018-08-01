package com.szzt.smart.framework.feign;


import com.szzt.smart.framework.constant.ResultCodeConstant;
import com.szzt.smart.framework.web.model.ResultBody;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cloud.netflix.feign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.netflix.hystrix.exception.HystrixTimeoutException;

import feign.Feign;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@ConditionalOnClass({FeignClientsConfiguration.class, Feign.class})
@AutoConfigureBefore(FeignClientsConfiguration.class)
public class FeignConfiguration
{

    @Bean
    public FeignExceptionHandler feginExceptionHandler()
    {
        return new FeignExceptionHandler();
    }

    public static class FeignExceptionHandler
    {

        private static final String LOAD_BALANCER_NOT_AVAILABLE = "not have available server";

        public ResultBody handleException(Throwable e)
        {
            log.error("feign error:", e);
            if (e instanceof HystrixTimeoutException)
            {
                return  ResultBody.fail(ResultCodeConstant.HTTP_INNTER_READ_TIME_OUT);
            }
            else if (e instanceof feign.RetryableException)
            {
                if (StringUtils.isNotBlank(e.getLocalizedMessage()))
                {
                    if (e.getLocalizedMessage().contains("Connection refused"))
                    {
                        return  ResultBody.fail(ResultCodeConstant.HTTP_INNTER_CONNECTION_TIME_OUT);
                    }
                    else if (e.getLocalizedMessage().contains("Read timed out"))
                    {
                        return  ResultBody.fail(ResultCodeConstant.HTTP_INNTER_READ_TIME_OUT);
                    }

                }

            }
            else if (e instanceof RuntimeException)
            {
                if (e.getCause() != null)
                {
                    String message = e.getCause().getLocalizedMessage();
                    if (StringUtils.isNotBlank(message) && message.contains(LOAD_BALANCER_NOT_AVAILABLE))
                    {
                        return  ResultBody.fail(ResultCodeConstant.HTTP_INNTER_SERVICE_NOT_AVAILABLE);
                    }
                }
            }
            return  ResultBody.fail(ResultCodeConstant.HTTP_INNTER_ERROR);
        }
    }

}

