package com.szzt.smart.framework.zookeeper;

import java.util.concurrent.TimeUnit;

import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

/**
 * Created by Hikaru on 17/8/24.
 */

@Validated
@ConfigurationProperties(prefix = "zookeeper")
@Data
public class LuZookeeperProperties {
  /**
   * Connection string to the Zookeeper cluster
   */
  @NotNull
  private String connectString = "localhost:2181";

  /**
   * Is Zookeeper enabled
   */
  private boolean enabled = true;

  /**
   * Initial amount of time to wait between retries
   */
  private Integer baseSleepTimeMs = 50;

  /**
   * Max number of times to retry
   */
  private Integer maxRetries = 10;

  /**
   * Max time in ms to sleep on each retry
   */
  private Integer maxSleepMs = 500;

  /**
   * Wait time to block on connection to Zookeeper
   */
  private Integer blockUntilConnectedWait = 10;

  /**
   * The unit of time related to blocking on connection to Zookeeper
   */
  private TimeUnit blockUntilConnectedUnit = TimeUnit.SECONDS;
}
