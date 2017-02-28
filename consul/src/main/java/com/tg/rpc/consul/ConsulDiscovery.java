package com.tg.rpc.consul;

import com.tg.rpc.core.servicecenter.Service;
import com.tg.rpc.core.servicecenter.ServiceChangeHandler;
import com.tg.rpc.core.servicecenter.ServiceDiscovery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-28
 */
public class ConsulDiscovery implements ServiceDiscovery {

    private static Logger log = LogManager.getLogger(ConsulDiscovery.class);

    private ConsulEcwidClient consulEcwidClient;
    private ConsulHeartbeatManger consulHeartbeatManger;
    private long lookupInterval;

    private ConcurrentHashMap<String, Service> serviceCache = new ConcurrentHashMap<>();

    public ConsulDiscovery(ConsulEcwidClient consulEcwidClient, ConsulHeartbeatManger consulHeartbeatManger) {
        this.consulEcwidClient = consulEcwidClient;
        this.consulHeartbeatManger = consulHeartbeatManger;
    }


    @Override
    public List<Service> discover(String serviceId) {
        return consulEcwidClient.lookupHealthService(serviceId);
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
