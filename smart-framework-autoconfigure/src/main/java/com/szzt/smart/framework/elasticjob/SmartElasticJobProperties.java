package com.szzt.smart.framework.elasticjob;

import org.hibernate.validator.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * Created by Hikaru on 17/9/6.
 */

@Validated
@Data
public class SmartElasticJobProperties {

  @NotBlank(message = "serverList 不能为空")
  private String serverList;
  @NotBlank(message = "namespace 不能为空")
  private String namespace;
  private String dataSource;
  private int baseSleepTimeMilliseconds = 1000;
  private int maxSleepTimeMilliseconds = 3000;
  private int maxRetries = 3;
  private int sessionTimeoutMilliseconds = 60000;
  private int connectionTimeoutMilliseconds = 15000;
  private String digest = "";


}
