package com.szzt.smart.framework.metrics;

public interface Influxdb {

  /**
   * 写库，返回成功or失败
   *
   * @return
   * @throws InfluxdbException
   */
  void writeData(SinglePoint singlePoint) throws InfluxdbException;

  /**
   * 写完调用flush清理缓冲
   *
   * @throws InfluxdbException
   */
  void flush() throws InfluxdbException;

}
