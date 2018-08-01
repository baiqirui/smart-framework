package com.szzt.smart.framework.metrics;

import java.lang.management.ManagementFactory;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

import com.szzt.smart.framework.util.HostIpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.*;
import com.google.common.collect.Maps;

public class InfluxdbReporter extends ScheduledReporter
{
    
    private static final Logger logger = LoggerFactory.getLogger(InfluxdbReporter.class);
    
    private static final String HOST_TAG_KEY = "host";
    
    private static final String APP_TAG_KEY = "appname";
    
    private static final String IP_TAG_KEY = "ip";
    
    /**
     * 为 {@link InfluxdbReporter} 返回一个 {@link Builder}
     *
     * @return {@link Builder} 实例
     */
    public static Builder forRegistry(MetricRegistry registry)
    {
        return new Builder(registry);
    }
    
    /**
     * 类似于consoleReporter
     */
    public static class Builder
    {
        
        private final MetricRegistry registry;
        
        private Clock clock;
        
        private TimeUnit rateUnit;
        
        private TimeUnit durationUnit;
        
        private MetricFilter filter;
        
        private Map<String, String> tagMap;
        
        private String appName;
        
        private Builder(MetricRegistry registry)
        {
            this.tagMap = new HashMap<>();
            tagMap.put(HOST_TAG_KEY, getHost(getMXBeanName()));
            tagMap.put(IP_TAG_KEY, HostIpUtil.hostIp);
            this.registry = registry;
            this.clock = Clock.defaultClock();
            this.rateUnit = TimeUnit.SECONDS;
            this.durationUnit = TimeUnit.MILLISECONDS;
            this.filter = MetricFilter.ALL;
        }
        
        /**
         * 设置应用名称,必填项
         *
         * @return {@code Builder}
         */
        public Builder appName(String name)
        {
            this.appName = name;
            tagMap.put(APP_TAG_KEY, name);
            return this;
        }
        
        public Builder addTag(String key, String value)
        {
            tagMap.put(key, value);
            return this;
        }
        
        /**
         * 用于获取时间
         *
         * @param clock a {@link Clock} instance
         * @return {@code Builder}
         */
        public Builder withClock(Clock clock)
        {
            this.clock = clock;
            return this;
        }
        
        /**
         * 依据传入时间单位作为频率
         *
         * @param rateUnit 时间单位
         * @return {@code Builder}
         */
        public Builder convertRatesTo(TimeUnit rateUnit)
        {
            this.rateUnit = rateUnit;
            return this;
        }
        
        /**
         * 用于记录耗时的时间单位
         *
         * @param durationUnit a unit of time
         * @return {@code Builder}
         */
        public Builder convertDurationsTo(TimeUnit durationUnit)
        {
            this.durationUnit = durationUnit;
            return this;
        }
        
        /**
         * 可以过滤指定名称的metric
         *
         * @param filter a {@link MetricFilter}
         * @return {@code Builder}
         */
        public Builder filter(MetricFilter filter)
        {
            this.filter = filter;
            return this;
        }
        
        /**
         * 移出默认tag:appName,host,ip
         */
        public Builder withOutDefaultTags()
        {
            tagMap.remove(HOST_TAG_KEY);
            tagMap.remove(APP_TAG_KEY);
            tagMap.remove(IP_TAG_KEY);
            return this;
        }
        
        /**
         * 构建 {@link InfluxdbReporter}
         *
         * @return a {@link InfluxdbReporter}
         */
        public InfluxdbReporter build(Influxdb influxdb)
        {
            if (influxdb == null)
            {
                throw new IllegalArgumentException("influxdb can not be null");
            }
            if (appName == null)
            {
                throw new IllegalArgumentException("appname can not be null");
            }
            return new InfluxdbReporter(registry, influxdb, clock, rateUnit, durationUnit, filter, tagMap, appName);
        }
    }
    
    private final Influxdb influxdb;
    
    private final Clock clock;
    
    private final Map<String, String> tagMap;
    
    private final String appName;
    
    private InfluxdbReporter(MetricRegistry registry, Influxdb influxdb, Clock clock, TimeUnit rateUnit,
        TimeUnit durationUnit, MetricFilter filter, Map<String, String> tagMap, String appName)
    {
        super(registry, "influxdb-reporter", filter, rateUnit, durationUnit);
        this.influxdb = influxdb;
        this.clock = clock;
        this.appName = appName;
        this.tagMap = Collections.unmodifiableMap(tagMap);
    }
    
    @Override
    public void report(SortedMap<String, Gauge> gauges, SortedMap<String, Counter> counters,
        SortedMap<String, Histogram> histograms, SortedMap<String, Meter> meters, SortedMap<String, Timer> timers)
    {
        try
        {
            long timestamp = convert2nanoseconds(clock.getTime());
            if (!gauges.isEmpty())
            {
                for (Map.Entry<String, Gauge> entry : gauges.entrySet())
                {
                    reportGauge(entry.getKey(), entry.getValue(), timestamp);
                }
            }
            
            if (!counters.isEmpty())
            {
                for (Map.Entry<String, Counter> entry : counters.entrySet())
                {
                    reportCounter(entry.getKey(), entry.getValue(), timestamp);
                }
            }
            
            if (!histograms.isEmpty())
            {
                for (Map.Entry<String, Histogram> entry : histograms.entrySet())
                {
                    reportHistogram(entry.getKey(), entry.getValue(), timestamp);
                }
            }
            
            if (!meters.isEmpty())
            {
                for (Map.Entry<String, Meter> entry : meters.entrySet())
                {
                    reportMeter(entry.getKey(), entry.getValue(), timestamp);
                }
            }
            
            if (!timers.isEmpty())
            {
                for (Map.Entry<String, Timer> entry : timers.entrySet())
                {
                    reportTimer(entry.getKey(), entry.getValue(), timestamp);
                }
            }
            influxdb.flush();
        }
        catch (InfluxdbException e)
        {
            logger.warn("Report metrics data failed,please make sure remote influxdb is OK", e);
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }
    
    private static long convert2nanoseconds(long millisecond)
    {
        return millisecond * 1000 * 1000;
    }
    
    private void reportMeter(String name, Meter meter, long timestamp)
        throws InfluxdbException
    {
        if (name.startsWith(MetricsConstants.PREFIX_INFLUXDB_METRICS))
        {
            name = StringUtils.removeFirst(name, MetricsConstants.PREFIX_INFLUXDB_METRICS);
            String meterName = makeName(appName, name, "meter");
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("count", meter.getCount());
            fieldMap.put("m1_rate", convertRate(meter.getOneMinuteRate()));
            fieldMap.put("m5_rate", convertRate(meter.getFiveMinuteRate()));
            fieldMap.put("m15_rate", convertRate(meter.getFifteenMinuteRate()));
            fieldMap.put("mean_rate", convertRate(meter.getMeanRate()));
            influxdb.writeData(buildSinglePoint(meterName, tagMap, fieldMap, timestamp));
        }
        
    }
    
    private void reportCounter(String name, Counter counter, long timestamp)
        throws InfluxdbException
    {
        if (name.startsWith(MetricsConstants.PREFIX_INFLUXDB_METRICS))
        {
            name = removeFirst(name, MetricsConstants.PREFIX_INFLUXDB_METRICS);
            String counterName = makeName(appName, name, "counter");
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("count", counter.getCount());
            influxdb.writeData(buildSinglePoint(counterName, tagMap, fieldMap, timestamp));
        }
    }
    
    private void reportGauge(String name, Gauge gauge, long timestamp)
        throws InfluxdbException
    {
        if (name.startsWith(MetricsConstants.PREFIX_INFLUXDB_METRICS))
        {
            name = removeFirst(name, MetricsConstants.PREFIX_INFLUXDB_METRICS);
            String gaugeName = makeName(appName, name, "gauge");
            Map<String, Object> fieldMap = new HashMap<>();
            if (gauge.getValue() instanceof Number)
            {
                if (name.startsWith("jvm."))
                {
                    gaugeName = makeName("", "jvm", "gauge");
                    fieldMap.put(removeFirst(name, "jvm.").replaceAll("\\.", "_"), gauge.getValue());
                }
                else
                {
                    fieldMap.put("value", gauge.getValue());
                }
                
                influxdb.writeData(buildSinglePoint(gaugeName, tagMap, fieldMap, timestamp));
            }
        }
        
    }
    
    private void reportHistogram(String name, Histogram histogram, long timestamp)
        throws InfluxdbException
    {
        if (name.startsWith(MetricsConstants.PREFIX_INFLUXDB_METRICS))
        {
            name = removeFirst(name, MetricsConstants.PREFIX_INFLUXDB_METRICS);
            String histogramName = makeName(appName, name, "histogram");
            final Snapshot snapshot = histogram.getSnapshot();
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("count", histogram.getCount());
            fieldMap.put("max", snapshot.getMax());
            fieldMap.put("mean", snapshot.getMean());
            fieldMap.put("min", snapshot.getMin());
            fieldMap.put("stddev", snapshot.getStdDev());
            fieldMap.put("p50", snapshot.getMedian());
            fieldMap.put("p75", snapshot.get75thPercentile());
            fieldMap.put("p95", snapshot.get95thPercentile());
            fieldMap.put("p98", snapshot.get98thPercentile());
            fieldMap.put("p99", snapshot.get99thPercentile());
            fieldMap.put("p999", snapshot.get999thPercentile());
            influxdb.writeData(buildSinglePoint(histogramName, tagMap, fieldMap, timestamp));
        }
    }
    
    private void reportTimer(String name, Timer timer, long timestamp)
        throws InfluxdbException
    {
        if (name.startsWith(MetricsConstants.PREFIX_INFLUXDB_METRICS))
        {
            name = removeFirst(name, MetricsConstants.PREFIX_INFLUXDB_METRICS);
            
            String timerName = makeName(appName, name, "timer");
            // 这是一个巨坑，因为report方法的调用是在父类ScheduledReporter 中 public void report()触发的
            // 而父类的report方法又是synchronized (this) ,采用谷歌jar去创建Map,正好要获取该对象的锁，
            // 因此在执行Map<String, String> newTagMap = Maps.newHashMap();方法是 正好这个线程就会发生死锁
            // Map<String, String> newTagMap = Maps.newHashMap();
            Map<String, String> newTagMap = new HashMap<>();
            if (name.startsWith(MetricsConstants.REQUESTMETRICS_PREFIX))
            {
                timerName = makeName("", MetricsConstants.REQUESTMETRICS_PREFIX, "timer");
                String[] values = removeFirst(name, MetricsConstants.REQUESTMETRICS_PREFIX).split("\\.");
                newTagMap.put("path", values[0]);
                newTagMap.put("code", values[1]);
            }
            final Snapshot snapshot = timer.getSnapshot();
            Map<String, Object> fieldMap = new HashMap<>();
            fieldMap.put("count", timer.getCount());
            fieldMap.put("mean_rate", convertRate(timer.getMeanRate()));
            fieldMap.put("m1_rate", convertRate(timer.getOneMinuteRate()));
            fieldMap.put("m5_rate", convertRate(timer.getFiveMinuteRate()));
            fieldMap.put("m15_rate", convertRate(timer.getFifteenMinuteRate()));
            
            fieldMap.put("max", convertDuration(snapshot.getMax()));
            fieldMap.put("mean", convertDuration(snapshot.getMean()));
            fieldMap.put("min", convertDuration(snapshot.getMin()));
            fieldMap.put("stddev", convertDuration(snapshot.getStdDev()));
            fieldMap.put("median", convertDuration(snapshot.getMedian()));
            fieldMap.put("p75", convertDuration(snapshot.get75thPercentile()));
            fieldMap.put("p95", convertDuration(snapshot.get95thPercentile()));
            fieldMap.put("p99", convertDuration(snapshot.get99thPercentile()));
            fieldMap.put("p999", convertDuration(snapshot.get999thPercentile()));
            newTagMap.putAll(tagMap);
            influxdb.writeData(buildSinglePoint(timerName, newTagMap, fieldMap, timestamp));
        }
    }
    
    private static SinglePoint buildSinglePoint(String name, Map<String, String> tagMap, Map<String, Object> fieldMap,
        long timestamp)
    {
        Map<String, Object> finalMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : fieldMap.entrySet())
        {
            if (entry.getValue() instanceof Number)
            {
                if (entry.getValue() instanceof Double)
                {
                    finalMap.put(entry.getKey(), String.format("%.2f", entry.getValue()));
                }
                else
                {
                    finalMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return SinglePoint.newBuilder(name).addAllTag(tagMap).addAllField(finalMap).setTimestamp(timestamp).build();
    }
    
    /**
     * 过滤点号,会造成查询时出错，点号会被认为是db与留存策略的分隔
     */
    private static String makeName(String appname, String name, String type)
    {
        return MetricRegistry.name(appname, name, type).replaceAll("\\.", "_");
    }
    
    private static String getHost(String pidHost)
    {
        int index = pidHost.indexOf('@');
        String retHost = String.valueOf(System.currentTimeMillis());
        if (index == -1)
        {
            retHost = pidHost;
        }
        else if (index + 1 <= pidHost.length())
        {
            retHost = pidHost.substring(index + 1);
        }
        return retHost.replaceAll("\\.", "_");
    }
    
    private static String getMXBeanName()
    {
        String pidHost = ManagementFactory.getRuntimeMXBean().getName();
        if (pidHost == null || pidHost.length() == 0)
        {
            return String.valueOf(System.currentTimeMillis());
        }
        return pidHost;
    }
    
    public static String removeFirst(String text, String regex)
    {
        return replaceFirst(text, regex, "");
    }
    
    public static String replaceFirst(String text, String regex, String replacement)
    {
        return text != null && regex != null && replacement != null ? text.replaceFirst(regex, replacement) : text;
    }
}
