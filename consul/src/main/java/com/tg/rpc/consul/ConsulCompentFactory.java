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

    public static ServiceRegistry getRegistry(String host, int port) {
        ConsulEcwidClient consulEcwidClient = new ConsulEcwidClient(host, port);
        return new ConsulRegistry(consulEcwidClient);
    }

    public static ServiceRegistry getRegistry() {
        ConsulEcwidClient consulEcwidClient = new ConsulEcwidClient(ConfigConstant.DEFAULT_CONSUL_HOST, ConfigConstant.DEFAULT_CONSUL_PORT);
        return new ConsulRegistry(consulEcwidClient);
    }

    public static ServiceDiscovery getDiscovery(String host, int port) {
        ConsulEcwidClient consulEcwidClient = new ConsulEcwidClient(host, port);
        return new ConsulDiscovery(consulEcwidClient);
    }

    public static ServiceDiscovery getDiscovery() {
        ConsulEcwidClient consulEcwidClient = new ConsulEcwidClient(ConfigConstant.DEFAULT_CONSUL_HOST, ConfigConstant.DEFAULT_CONSUL_PORT);
        return new ConsulDiscovery(consulEcwidClient);
    }
}
