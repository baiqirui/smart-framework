package com.szzt.smart.framework.elasticjob;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface SmartElasticJob {

  String cron() default "";

  int shardingTotalCount() default  1;

  String shardingItemParameters() default "";

  boolean overwrite() default true;

  boolean disabled() default false;

  boolean streamingProcess() default false;

  String scriptCommandLine() default "";

  Class<? extends ElasticJobListener> [] elasticJobListeners() default {};

  String jobShardingStrategyClass() default "";

}
