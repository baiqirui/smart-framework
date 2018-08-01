package com.szzt.smart.framework.kafka;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.messaging.Message;

import com.szzt.smart.framework.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2017/8/24.
 */
@Slf4j
public class LuMessageConverter extends MessagingMessageConverter
{
    
    @Override
    protected Object convertPayload(Message<?> message)
    {
        return JsonUtil.obj2string(message.getPayload());
    }
    
    @Override
    protected Object extractAndConvertValue(ConsumerRecord<?, ?> record, Type type)
    {
        Object value = record.value();
        if (record.value() == null)
        {
            return KafkaNull.INSTANCE;
        }
        else
        {
            String realValue;
            if (value instanceof byte[])
            {
                realValue = new String((byte[])value, StandardCharsets.UTF_8);
            }
            else if (value instanceof String)
            {
                realValue = (String)value;
            }
            else
            {
                throw new IllegalStateException("Only byte[] and String supported");
            }
            
            String topic = record.topic();
            if (topic.equals(LuKafkaConstants.DELAY_TOPIC))
            {
                try
                {
                    LuDelayMessage delayMessage = JsonUtil.str2obj(realValue, LuDelayMessage.class);
                    log.info("delay message is {}",
                        LuKafkaConstants.createDetailMessage(record, delayMessage.toString()));
                    return delayMessage;
                }
                catch (Exception e)
                {
                    log.warn("JsonFormat fail, maybe producer not use framework, message is {}",
                        LuKafkaConstants.createDetailMessage(record, realValue));
                    return LuDelayMessage.builder().build();
                }
            }
            try
            {
                LuImmediateMessage message = JsonUtil.str2obj(realValue, LuImmediateMessage.class);
                log.info("immediately message is {}",
                    LuKafkaConstants.createDetailMessage(record, message.getMessage()));
                return message;
            }
            catch (Exception e)
            {
                return realValue;
            }
        }
    }
}
