package com.szzt.smart.framework.kafka;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

import com.szzt.smart.framework.util.JsonUtil;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created on 2017/8/24.
 */
@Data
@Slf4j
@AllArgsConstructor
public class LuKafkaProducer
{
    
    private KafkaTemplate<byte[], byte[]> kafkaTemplate;
    
    public ListenableFuture<SendResult<byte[], byte[]>> sendCreateDelayMessage(String topic, String message,
        LocalDateTime launchDateTime, String opid)
    {
        Assert.notNull(opid, "opid is null");
        LuDelayMessage delayMessage = LuDelayMessage.builder()
            .message(message)
            .topic(topic)
            .launchTime(launchDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
            .operate(DelayMessageOperate.CREATE)
            .opid(opid)
            .build();
        try
        {
            return kafkaTemplate.send(LuKafkaConstants.DELAY_TOPIC,
                JsonUtil.obj2string(delayMessage).getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e)
        {
            throw new LuKafkaProducerException(e.getMessage(), e);
        }
    }
    
    public ListenableFuture<SendResult<byte[], byte[]>> sendImmediate(String topic, String message)
    {
        try
        {
            LuImmediateMessage immediateMessage = new LuImmediateMessage();
            immediateMessage.setMessage(message);
            immediateMessage.setOpid(UUID.randomUUID().toString());
            
            return kafkaTemplate.send(topic, JsonUtil.obj2string(immediateMessage).getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e)
        {
            throw new LuKafkaProducerException(e.getMessage(), e);
        }
    }
    
    public ListenableFuture<SendResult<byte[], byte[]>> sendImmediate(String topic, String message, String opid)
    {
        try
        {
            LuImmediateMessage immediateMessage = new LuImmediateMessage();
            immediateMessage.setMessage(message);
            immediateMessage.setOpid(opid);
            return kafkaTemplate.send(topic, JsonUtil.obj2string(immediateMessage).getBytes(StandardCharsets.UTF_8));
        }
        catch (Exception e)
        {
            throw new LuKafkaProducerException(e.getMessage(), e);
        }
    }
}
