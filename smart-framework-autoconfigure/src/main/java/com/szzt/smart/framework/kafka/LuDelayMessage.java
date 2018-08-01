package com.szzt.smart.framework.kafka;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created on 2017/8/24.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LuDelayMessage {
  private String message;
  private String topic;
  private long launchTime;
  private DelayMessageOperate operate;
  private String opid;

  final private String randomStr = UUID.randomUUID().toString();
  final private String messageType = LuKafkaConstants.DELAY_MESSAGE_TYPE;
  final private String signature = LuKafkaConstants.MESSAGE_SIGNATURE;
}
