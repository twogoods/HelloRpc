package com.tg.rpc.consul;

import com.tg.rpc.core.entity.ConfigConstant;
import com.tg.rpc.core.servicecenter.ServiceDiscovery;
import com.tg.rpc.core.servicecenter.ServiceRegistry;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-03-01
 */
public class ConsulCompentFactory {

    public static ServiceRegistry getRegistry(String host, int port, long ttl) {
        ConsulEcwidClient consulEcwidClient = new ConsulEcwidClient(host, port);
        return new ConsulRegistry(consulEcwidClient, ttl);
    }

    public static ServiceRegistry getRegistry(String host, int port) {
        return getRegistry(host, port, ConfigConstant.DEFAULT_TTL);
    }

    public static ServiceRegistry getRegistry() {
        return getRegistry(ConfigConstant.DEFAULT_CONSUL_HOST, ConfigConstant.DEFAULT_CONSUL_PORT, ConfigConstant.DEFAULT_TTL);
    }

    public static ServiceDiscovery getDiscovery(String host, int port) {
        ConsulEcwidClient consulEcwidClient = new ConsulEcwidClient(host, port);
        return new ConsulDiscovery(consulEcwidClient);
    }

    public static ServiceDiscovery getDiscovery() {
        return getDiscovery(ConfigConstant.DEFAULT_CONSUL_HOST, ConfigConstant.DEFAULT_CONSUL_PORT);
    }
}
