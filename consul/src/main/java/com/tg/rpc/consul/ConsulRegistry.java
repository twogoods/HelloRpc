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

    public ConsulRegistry(ConsulEcwidClient consulEcwidClient) {
        this.consulEcwidClient = consulEcwidClient;
        this.consulHeartbeatManger = new ConsulHeartbeatManger(consulEcwidClient);
    }

    @Override
    public void register(Service service) {
        consulEcwidClient.registerService(service);
        consulHeartbeatManger.addHeartbeatService(service.getId());
    }

    @Override
    public void unregister(Service service) {
        consulEcwidClient.unregisterService(service.getId());
        consulHeartbeatManger.removeHeartbeatService(service.getId());
    }
}
