package com.szzt.smart.framework.zookeeper;

import java.util.List;
import java.util.Optional;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCache.StartMode;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.zookeeper.data.Stat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by Hikaru on 17/8/24.
 */

@Data
@AllArgsConstructor
@Slf4j
public class LuZookeeperOper {

  private CuratorFramework curatorFramework;

  public void addPathChildrenCacheListener(String parentPath,
      PathChildrenCacheEventHandler pathChildrenCacheEventHandler) {
    PathChildrenCache childrenCache = new PathChildrenCache(curatorFramework, parentPath, true);
    PathChildrenCacheListener childrenCacheListener = (client, event) -> {
      ChildData data = event.getData();
      switch (event.getType()) {
        case CHILD_ADDED:
          pathChildrenCacheEventHandler.childAdd(data);
          break;
        case CHILD_REMOVED:
          pathChildrenCacheEventHandler.childRemove(data);
          break;
        case CHILD_UPDATED:
          pathChildrenCacheEventHandler.childUpdate(data);
          break;
        case CONNECTION_RECONNECTED:
          pathChildrenCacheEventHandler.connectionReConnected(data);
          break;
        case CONNECTION_SUSPENDED:
          pathChildrenCacheEventHandler.connectionSuspended(data);
          break;
        case CONNECTION_LOST:
          pathChildrenCacheEventHandler.connectionLost(data);
          break;
        default:
          break;
      }

    };

    try {
      childrenCache.getListenable().addListener(childrenCacheListener);
      childrenCache.start(StartMode.POST_INITIALIZED_EVENT);
      log.info("addPathChildrenCacheListener：{}", parentPath);
    } catch (Exception e) {
      log.error("addPathChildrenCacheListener Error", e);
    }
  }


  public void addTreeCacheListener(String parentPath, TreeCacheHandler treeCacheHandler) {
    TreeCache treeCache = new TreeCache(curatorFramework, parentPath);
    treeCache.getListenable().addListener((client, event) -> {
      ChildData data = event.getData();
      if (data != null) {
        switch (event.getType()) {
          case NODE_ADDED:
            treeCacheHandler.nodeAdd(data);
            break;
          case NODE_REMOVED:
            treeCacheHandler.nodeRemoved(data);
            break;
          case NODE_UPDATED:
            treeCacheHandler.nodeUpdated(data);
            break;
          case CONNECTION_RECONNECTED:
            treeCacheHandler.connectionReConnected(data);
            break;
          case CONNECTION_SUSPENDED:
            treeCacheHandler.connectionSuspended(data);
            break;
          case CONNECTION_LOST:
            treeCacheHandler.connectionLost(data);
            break;

          default:
            break;
        }
      } else {
        log.warn("data is null : {}", event.getType());
      }
    });
    //开始监听
    try {
      treeCache.start();
    } catch (Exception e) {
      log.error("addTreeCacheListener Error", e);
    }
  }

  public String create(String path, String data) {
    try {
      return this.curatorFramework.create().creatingParentsIfNeeded()
          .forPath(path, data.getBytes());
    } catch (Exception e) {
      throw new LuZookeeperException(e);
    }
  }

  public List<String> getNode(String path) {
    try {
      return this.curatorFramework.getChildren().forPath(path);
    } catch (Exception e) {
      throw new LuZookeeperException(e);
    }
  }


  public byte[] getData(String path) {
    try {
      return this.curatorFramework.getData().forPath(path);
    } catch (Exception e) {
      throw new LuZookeeperException(e);
    }
  }

  public Stat updateData(String path, String data) {
    try {
      return this.curatorFramework.setData().forPath(path, data.getBytes());
    } catch (Exception e) {
      throw new LuZookeeperException(e);
    }
  }

  public void delete(String path) {
    try {
      this.curatorFramework.delete().forPath(path);
    } catch (Exception e) {
      throw new LuZookeeperException(e);
    }

  }

  public boolean checkExists(String path) {
    try {
      return Optional.ofNullable(this.curatorFramework.checkExists().forPath(path)).isPresent();
    } catch (Exception e) {
      throw new LuZookeeperException(e);
    }
  }


}
