package com.szzt.smart.framework.metrics;

public interface MetricsConstants
{
    
    // metrics
    String REQUESTMETRICS_PREFIX = "_RequestMetrics"; // 请求qps记录的前缀
    
    String PREFIX_INFLUXDB_METRICS = "_InFluxDB_Metrics.";
    
    String PROP_METRIC_REG_JVM_MEMORY = PREFIX_INFLUXDB_METRICS + "jvm.memory";
    
    String PROP_METRIC_REG_JVM_GARBAGE = PREFIX_INFLUXDB_METRICS + "jvm.garbage";
    
    String PROP_METRIC_REG_JVM_THREADS = PREFIX_INFLUXDB_METRICS + "jvm.threads";
    
    String PROP_METRIC_REG_JVM_FILES = PREFIX_INFLUXDB_METRICS + "jvm.files";
    
    String PROP_METRIC_REG_JVM_BUFFERS = PREFIX_INFLUXDB_METRICS + "jvm.buffers";
    
}
