package com.szzt.smart.framework.metrics;

public class InfluxdbException extends Exception {

  public InfluxdbException() {}

  public InfluxdbException(String msg) {
    super(msg);
  }

  public InfluxdbException(String msg, Throwable cause) {
    super(msg, cause);
  }

}