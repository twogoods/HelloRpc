package com.tg.rpc.zookeeper;

import com.tg.rpc.core.servicecenter.Service;
import com.tg.rpc.core.servicecenter.ServiceChangeHandler;
import com.tg.rpc.core.servicecenter.ServiceDiscovery;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.details.ServiceCacheListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-03-03
 */
public class ZookeeperDiscovery implements ServiceDiscovery {

    private static final Logger log = LoggerFactory.getLogger(ZookeeperDiscovery.class);

    private final org.apache.curator.x.discovery.ServiceDiscovery<Service> zkDiscovery;
    private CuratorFramework client;

    public ZookeeperDiscovery(String host, int port, String serverPath) throws Exception {
        this.client = CuratorFrameworkFactory.newClient(host + ":" + port, new ExponentialBackoffRetry(1000, 3));
        client.start();
        JsonInstanceSerializer<Service> serializer = new JsonInstanceSerializer<Service>(Service.class);
        this.zkDiscovery = ServiceDiscoveryBuilder.builder(Service.class)
                .client(client)
                .basePath(serverPath)
                .serializer(serializer)
                .build();
        zkDiscovery.start();
    }

    @Override
    public List<Service> discover(String serviceName) throws Exception {
        Collection<ServiceInstance<Service>> instances = zkDiscovery.queryForInstances(serviceName);
        List<Service> services = new ArrayList<>(instances.size());
        for (ServiceInstance<Service> instance : instances) {
            services.add(convertToService(instance));
        }
        return services;
    }

    @Override
    public void addListener(final String serviceName, final ServiceChangeHandler handler) throws Exception {
        ServiceCache cache = zkDiscovery.serviceCacheBuilder().name(serviceName).build();
        cache.addListener(new ServiceCacheListener() {
                              @Override
                              public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                                  log.debug("service : {} stateChanged!", serviceName);
                                  try {
                                      List<Service> services = discover(serviceName);
                                      handler.handle(services);
                                  } catch (Exception e) {
                                      log.error("discover server error in listener :{}", e);
                                  }
                              }

                              @Override
                              public void cacheChanged() {
                                  log.debug("service : {} has Changed! reload...", serviceName);
                                  try {
                                      List<Service> services = discover(serviceName);
                                      handler.handle(services);
                                  } catch (Exception e) {
                                      log.error("discover server error in listener :{}", e);
                                  }
                              }
                          }
        );
        cache.start();
    }

    public void close() {
        try {
            zkDiscovery.close();
            client.close();
        } catch (IOException e) {
            log.error("ZookeeperDiscovery close error : {}", e);
        }
    }

    private Service convertToService(ServiceInstance<Service> instance) {
        Service service = new Service();
        service.setId(instance.getId());
        service.setName(instance.getName());
        service.setAddress(instance.getAddress());
        service.setPort(instance.getPort());
        return service;
    }

}
