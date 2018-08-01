package com.szzt.smart.framework.kafka;

import java.util.UUID;

import lombok.*;

/**
 * Created on 2017/8/24.
 */
public class LuImmediateMessage {
  @NonNull
  @Setter
  @Getter
  private String message;

  @NonNull
  @Setter
  @Getter
  private String opid;

  @Getter(value = AccessLevel.PACKAGE)
  final private String randomStr = UUID.randomUUID().toString();
  @Getter(value = AccessLevel.PACKAGE)
  final private String messageType = LuKafkaConstants.IMMEDIATE_MESSAGE_TYPE;
  @Getter(value = AccessLevel.PACKAGE)
  final private String signature = LuKafkaConstants.MESSAGE_SIGNATURE;
}
