package com.szzt.smart.framework.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;

/**
 * Created on 2017/8/24.
 */
public interface LuKafkaConstants {
  String DELAY_TOPIC = "delay_topic";

  String DELAY_MESSAGE_TYPE = "delay_type";
  String IMMEDIATE_MESSAGE_TYPE = "immediate_type";

  String MESSAGE_SIGNATURE = "Jw@x2M9!d";

  static String createDetailMessage(ConsumerRecord<?, ?> record, String message) {
    return String
        .format("topic = %s, partition = %s, offset = %s, timestamp = %s, message = %s",
            record.topic(), record.partition(), record.offset(), record.timestamp(),
            message);
  }
}
