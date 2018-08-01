package com.szzt.smart.framework.kafka;

import java.nio.charset.StandardCharsets;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.adapter.RecordFilterStrategy;
import org.springframework.kafka.listener.config.ContainerProperties;

import com.szzt.smart.framework.util.JsonUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2017/8/24.
 */
@Slf4j
@Configuration
@ConditionalOnClass({KafkaTemplate.class, EnableKafka.class})
@AutoConfigureAfter({KafkaAutoConfiguration.class})
public class LuKafkaConfiguration
{
    
    @Bean
    @ConditionalOnMissingBean
    public LuKafkaProducer luKafkaProducer(KafkaTemplate<byte[], byte[]> kafkaTemplate)
    {
        return new LuKafkaProducer(kafkaTemplate);
    }
    
    @Bean(name = "kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
        ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
        ConsumerFactory<Object, Object> kafkaConsumerFactory)
    {
        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        ContainerProperties containerProperties = factory.getContainerProperties();
        containerProperties.setErrorHandler(new LuKafkaErrorHandler());
        factory.setRecordFilterStrategy(recordFilterStrategy());
        factory.setMessageConverter(new LuMessageConverter());
        configurer.configure(factory, kafkaConsumerFactory);
        return factory;
    }
    
    private RecordFilterStrategy<Object, Object> recordFilterStrategy()
    {
        return (consumerRecord) -> {
            String topic = consumerRecord.topic();
            Object value = consumerRecord.value();
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
                log.error("Only byte[] or string supported");
                return true;
            }
            
            try
            {
                if (topic.equals(LuKafkaConstants.DELAY_TOPIC))
                {
                    LuDelayMessage delayMessage = JsonUtil.str2obj(realValue, LuDelayMessage.class);
                    return !(delayMessage.getMessageType().equals(LuKafkaConstants.DELAY_MESSAGE_TYPE)
                        && delayMessage.getSignature().equals(LuKafkaConstants.MESSAGE_SIGNATURE));
                }
                else
                {
                    try
                    {
                        LuImmediateMessage immediateMessage = JsonUtil.str2obj(realValue, LuImmediateMessage.class);
                        return !(immediateMessage.getMessageType().equals(LuKafkaConstants.IMMEDIATE_MESSAGE_TYPE)
                            && immediateMessage.getSignature().equals(LuKafkaConstants.MESSAGE_SIGNATURE));
                    }
                    catch (RuntimeException e)
                    {
                        log.error("kafka消费出错", e);
                        log.warn("JsonFormat fail, maybe producer not use framework");
                        return false;
                    }
                }
            }
            catch (Exception e)
            {
                log.error(e.getMessage(), e);
            }
            return true;
        };
    }
}
