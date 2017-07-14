package com.tg.rpc.zookeeper;

import com.tg.rpc.core.entity.ConfigConstant;
import com.tg.rpc.core.servicecenter.ServiceDiscovery;
import com.tg.rpc.core.servicecenter.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-03-03
 */
public class ZookeeperCompentFactory {
    private static final Logger log = LoggerFactory.getLogger(ZookeeperCompentFactory.class);

    public static ServiceRegistry getRegistry(String zkHost, int zkPort, String servicePath) {
        try {
            return new ZookeeperRegistry(zkHost, zkPort, servicePath);
        } catch (Exception e) {
            log.error("instance ZookeeperRegistry error : {}", e);
        }
        return null;
    }

    public static ServiceRegistry getRegistry(String zkHost, int zkPort) {
        return getRegistry(zkHost, zkPort, ConfigConstant.DEFAULT_ZOOKEEPER_SERVICE_PATH);
    }

    public static ServiceRegistry getRegistry() {
        return getRegistry(ConfigConstant.DEFAULT_ZOOKEEPER_HOST, ConfigConstant.DEFAULT_ZOOKEEPER_PORT, ConfigConstant.DEFAULT_ZOOKEEPER_SERVICE_PATH);
    }

    public static ServiceDiscovery getDiscovery(String zkHost, int zkPort, String servicePath) {
        try {
            return new ZookeeperDiscovery(zkHost, zkPort, servicePath);
        } catch (Exception e) {
            log.error("instance ZookeeperDiscovery error : {}", e);
        }
        return null;
    }

    public static ServiceDiscovery getDiscovery(String zkHost, int zkPort) {
        return getDiscovery(zkHost, zkPort, ConfigConstant.DEFAULT_ZOOKEEPER_SERVICE_PATH);
    }

    public static ServiceDiscovery getDiscovery() {
        return getDiscovery(ConfigConstant.DEFAULT_ZOOKEEPER_HOST, ConfigConstant.DEFAULT_ZOOKEEPER_PORT, ConfigConstant.DEFAULT_ZOOKEEPER_SERVICE_PATH);
    }
}
