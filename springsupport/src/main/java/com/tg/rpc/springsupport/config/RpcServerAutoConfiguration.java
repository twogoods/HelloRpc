package com.tg.rpc.springsupport.config;

import com.tg.rpc.consul.ConsulCompentFactory;
import com.tg.rpc.core.bootstrap.Server;
import com.tg.rpc.core.servicecenter.Registry;
import com.tg.rpc.core.servicecenter.ServiceRegistry;
import com.tg.rpc.springsupport.bean.server.SpringBeanResponseHandler;
import com.tg.rpc.zookeeper.ZookeeperCompentFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by twogoods on 17/2/21.
 */
@Configuration
@EnableConfigurationProperties(RpcServerConfig.class)
public class RpcServerAutoConfiguration {

    private final RpcServerConfig rpcServerConfig;

    public RpcServerAutoConfiguration(RpcServerConfig rpcServerConfig) {
        this.rpcServerConfig = rpcServerConfig;
    }

    @Bean
    public SpringBeanResponseHandler springBeanResponseHandler() {
        return new SpringBeanResponseHandler();
    }

    @Bean
    public Server server() {
        Server server;
        if (Registry.DEFAULT == rpcServerConfig.getRegistery()) {
            System.out.println("in default " + rpcServerConfig);
            server = serverWithoutregistry();
        } else if (Registry.CONSUL == rpcServerConfig.getRegistery()) {
            System.out.println("in consul " + rpcServerConfig);
            server = serverWithConsul();
        } else if (Registry.ZOOKEEPER == rpcServerConfig.getRegistery()) {
            server = serverWithZookeeper();
        } else {
            throw new IllegalArgumentException("properity Registry illega");
        }
        server.start();
        return server;
    }


    private Server serverWithoutregistry() {
        return new Server.Builder().port(rpcServerConfig.getPort())
                .serviceId(rpcServerConfig.getServiceId())
                .serviceName(rpcServerConfig.getServiceName())
                .maxCapacity(rpcServerConfig.getMaxCapacity())
                .responseHandler(springBeanResponseHandler())
                .build();
    }

    private Server serverWithConsul() {
        ServiceRegistry serviceRegistry = ConsulCompentFactory.getRegistry(rpcServerConfig.getConsulHost(), rpcServerConfig.getConsulPort(), rpcServerConfig.getTtl());
        return new Server.Builder().serviceRegistry(serviceRegistry)
                .serviceId(rpcServerConfig.getServiceId())
                .serviceName(rpcServerConfig.getServiceName())
                .maxCapacity(rpcServerConfig.getMaxCapacity())
                .responseHandler(springBeanResponseHandler())
                .build();
    }

    private Server serverWithZookeeper() {
        ServiceRegistry serviceRegistry = ZookeeperCompentFactory.getRegistry(rpcServerConfig.getZookeeperHost(), rpcServerConfig.getZookeeperPort(), rpcServerConfig.getZkServicePath());
        return new Server.Builder().serviceRegistry(serviceRegistry)
                .serviceId(rpcServerConfig.getServiceId())
                .serviceName(rpcServerConfig.getServiceName())
                .maxCapacity(rpcServerConfig.getMaxCapacity())
                .responseHandler(springBeanResponseHandler())
                .build();
    }


}
