package com.szzt.smart.framework.kafka;

public class LuKafkaProducerException extends RuntimeException {

  public LuKafkaProducerException(String message, Throwable e){
    super(message, e);
  }

  public LuKafkaProducerException(Throwable e){
    super(e);
  }

}
