package com.tg.rpc.consul;

import com.tg.rpc.core.servicecenter.Service;
import com.tg.rpc.core.servicecenter.ServiceRegistry;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-28
 */
public class ConsulRegistry implements ServiceRegistry {

    private ConsulEcwidClient consulEcwidClient;
    private ConsulHeartbeatManger consulHeartbeatManger;
    private long ttl;

    public ConsulRegistry(ConsulEcwidClient consulEcwidClient, long ttl) {
        this(consulEcwidClient, new ConsulHeartbeatManger(consulEcwidClient), ttl);
    }

    public ConsulRegistry(ConsulEcwidClient consulEcwidClient, ConsulHeartbeatManger consulHeartbeatManger, long ttl) {
        this.ttl = ttl;
        this.consulEcwidClient = consulEcwidClient;
        this.consulHeartbeatManger = consulHeartbeatManger;
    }

    @Override
    public void register(Service service) {
        consulEcwidClient.registerService(service);
        consulHeartbeatManger.setHeartbeatService(service);
        consulHeartbeatManger.start();
    }

    @Override
    public void unregister(Service service) {
        consulEcwidClient.unregisterService(service.getId());
        consulHeartbeatManger.removeHeartbeatService();
    }

    @Override
    public long getTTL() {
        return ttl;
    }

    @Override
    public void close() {

    }
}
