package com.tg.rpc.consul;

import com.tg.rpc.core.servicecenter.Service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-22
 */
public class ConsulHeartbeatManger {
    private ConsulEcwidClient consulEcwidClient;
    private Service service;

    ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor();

    public ConsulHeartbeatManger(ConsulEcwidClient consulEcwidClient) {
        this.consulEcwidClient = consulEcwidClient;
    }

    public void setHeartbeatService(Service service) {
        this.service = service;
    }

    public void removeHeartbeatService() {
        heartbeatExecutor.shutdownNow();
    }

    public void start() {
        if(service!=null){
            heartbeatExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    consulEcwidClient.checkPass(service.getId());
                }
            }, 0, service.getTtl(), TimeUnit.MILLISECONDS);
        }
    }
}
