package com.tg.rpc.consul;

import com.tg.rpc.core.entity.ConfigConstant;
import com.tg.rpc.core.servicecenter.Service;
import com.tg.rpc.core.servicecenter.ServiceChangeHandler;
import com.tg.rpc.core.servicecenter.ServiceDiscovery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-28
 */
public class ConsulDiscovery implements ServiceDiscovery {

    private static Logger log = LoggerFactory.getLogger(ConsulDiscovery.class);

    private ConsulEcwidClient consulEcwidClient;
    private long lookupInterval = ConfigConstant.DEFAULT_CONSUL_LOOKUPINTERVAL;

    public ConsulDiscovery(ConsulEcwidClient consulEcwidClient) {
        this.consulEcwidClient = consulEcwidClient;
    }

    public ConsulDiscovery(ConsulEcwidClient consulEcwidClient, long lookupInterval) {
        this.consulEcwidClient = consulEcwidClient;
        this.lookupInterval = lookupInterval;
    }

    @Override
    public List<Service> discover(String serviceName) {
        return consulEcwidClient.lookupHealthService(serviceName);
    }

    @Override
    public void addListener(String serviceName, ServiceChangeHandler handler) {
        ServiceLookupThread thread = new ServiceLookupThread(serviceName, handler);
        thread.setDaemon(true);
        thread.start();
    }

    private class ServiceLookupThread extends Thread {
        private ServiceChangeHandler handler;
        private String serviceName;

        public ServiceLookupThread(String serviceName, ServiceChangeHandler handler) {
            this.handler = handler;
            this.serviceName = serviceName;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    sleep(lookupInterval);
                    handler.handle(consulEcwidClient.lookupHealthService(serviceName));
                } catch (InterruptedException e) {
                    log.error("ServiceLookupThread error:{}", e);
                }
            }
        }
    }
}
