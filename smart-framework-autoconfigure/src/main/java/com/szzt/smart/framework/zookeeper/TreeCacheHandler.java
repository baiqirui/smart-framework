package com.szzt.smart.framework.zookeeper;

import org.apache.curator.framework.recipes.cache.ChildData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Hikaru on 17/8/24.
 */
public interface TreeCacheHandler {

  Logger logger = LoggerFactory.getLogger(TreeCacheHandler.class.getSimpleName());

  default void nodeAdd(ChildData data) {
    logger.info("NODE_ADDED : {},数据: {}", data.getPath(), new String(data.getData()));
  }

  default void nodeRemoved(ChildData data) {
    logger.info("NODE_REMOVED : {},数据: {}", data.getPath(), new String(data.getData()));
  }

  default void nodeUpdated(ChildData data) {
    logger.info("NODE_UPDATED : {},数据: {}", data.getPath(), new String(data.getData()));
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
