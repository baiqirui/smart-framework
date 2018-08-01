package com.szzt.smart.framework.zookeeper;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Hikaru on 17/8/24.
 */

public interface PathChildrenCacheEventHandler {

  Logger logger = LoggerFactory.getLogger(PathChildrenCacheEventHandler.class.getSimpleName());

  default void childAdd(ChildData data) {
    logger.info("CHILD_ADDED : {}  数据: {}", data.getPath(), data.getData());
  }

  default void childRemove(ChildData data) {
    logger.info("CHILD_REMOVED : {}  数据: {}", data.getPath(), data.getData());
  }

  default void childUpdate(ChildData data) {
    logger.info("CHILD_UPDATED : {}  数据: {}", data.getPath(), data.getData());
  }

  default void connectionReConnected(ChildData data) {
    logger.error("CONNECTION_RECONNECTED : {}", data.getPath());
  }

  default void connectionSuspended(ChildData data) {
    logger.error("CONNECTION_SUSPENDED : {}", data.getPath());
  }

  default void connectionLost(ChildData data) {
    logger.error("CONNECTION_LOST : {}", data.getPath());
  }

}
