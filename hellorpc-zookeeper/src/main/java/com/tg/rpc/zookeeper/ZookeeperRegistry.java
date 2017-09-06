package com.tg.rpc.zookeeper;

import com.tg.rpc.core.servicecenter.Service;
import com.tg.rpc.core.servicecenter.ServiceRegistry;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-03-03
 */
public class ZookeeperRegistry implements ServiceRegistry {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperRegistry.class);

    private ServiceDiscovery<Service> serviceDiscovery;
    private CuratorFramework client;

    public ZookeeperRegistry(String zkHost, int zkPort, String serverPath) throws Exception {
        this.client = CuratorFrameworkFactory.newClient(zkHost + ":" + zkPort, new ExponentialBackoffRetry(1000, 3));
        client.start();
        JsonInstanceSerializer<Service> serializer = new JsonInstanceSerializer<Service>(Service.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(Service.class)
                .client(client)
                .serializer(serializer)
                .basePath(serverPath)
                .build();
        serviceDiscovery.start();
    }

    @Override
    public void register(Service service) throws Exception {
        serviceDiscovery.registerService(convertServiceInstance(service));
    }

    @Override
    public void unregister(Service service) throws Exception {
        serviceDiscovery.unregisterService(convertServiceInstance(service));
    }

    @Override
    public long getTTL() {
        return 0;
    }

    @Override
    public void close() {
        try {
            serviceDiscovery.close();
            client.close();
        } catch (IOException e) {
            log.error("ZookeeperRegistry close error : {}", e);
        }
    }


    private ServiceInstance<Service> convertServiceInstance(Service service) throws Exception {
        return ServiceInstance.<Service>builder()
                .id(service.getId())
                .name(service.getName())
                .address(service.getAddress())
                .port(service.getPort())
                .build();
    }
}
