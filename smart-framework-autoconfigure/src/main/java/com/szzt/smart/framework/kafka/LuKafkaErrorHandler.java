package com.szzt.smart.framework.kafka;

import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.ErrorHandler;

import com.szzt.smart.framework.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2017/9/18.
 */
@Slf4j
public class LuKafkaErrorHandler implements ErrorHandler
{
    
    @Override
    public void handle(Exception thrownException, ConsumerRecord<?, ?> record)
    {
        Object value = record.value();
        String realValue;
        if (value instanceof byte[])
        {
            realValue = new String((byte[])value, StandardCharsets.UTF_8);
            try
            {
                String message = JsonUtil.str2obj(realValue, LuImmediateMessage.class).getMessage();
                log.error("consumer produce error, detail record is {}",
                    LuKafkaConstants.createDetailMessage(record, message));
            }
            catch (Exception e)
            {
                log.error("JsonFormat to ImmediateMessage error, detail record is {}",
                    LuKafkaConstants.createDetailMessage(record, realValue));
            }
        }
    }
}
