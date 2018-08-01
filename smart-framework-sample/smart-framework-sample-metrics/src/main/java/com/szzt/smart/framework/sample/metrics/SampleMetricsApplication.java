package com.szzt.smart.framework.sample.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@ComponentScan({"com.szzt.smart", "com.szzt.smart.framework.sample"})
@Slf4j
public class SampleMetricsApplication
{
    /**
     * https://www.jianshu.com/p/a1344ca86e9b
     * https://www.linuxdaxue.com/influxdb-basic-operation.html
     * https://blog.csdn.net/u010185262/article/details/53158786
     * @param args
     */
    public static void main(String[] args)
    {
        SpringApplication.run(SampleMetricsApplication.class, args);
    }
    
    // @Bean(destroyMethod = "close")
    // Slf4jReporter slf4jReporter(MetricRegistry metricRegistry) {
    // Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry)
    // .convertRatesTo(TimeUnit.SECONDS)
    // .convertDurationsTo(TimeUnit.MILLISECONDS)
    // .build();
    // reporter.start(1, TimeUnit.SECONDS);
    //
    // return reporter;
    // }
    
}
