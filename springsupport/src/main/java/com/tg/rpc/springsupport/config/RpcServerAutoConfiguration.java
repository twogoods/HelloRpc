package com.tg.rpc.springsupport.config;

import com.tg.rpc.consul.ConsulCompentFactory;
import com.tg.rpc.core.bootstrap.Server;
import com.tg.rpc.core.servicecenter.Registry;
import com.tg.rpc.core.servicecenter.ServiceRegistry;
import com.tg.rpc.springsupport.bean.server.SpringBeanResponseHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by twogoods on 17/2/21.
 */
@Configuration
@EnableConfigurationProperties(RpcConfig.class)
public class RpcServerAutoConfiguration {


    private final RpcConfig rpcConfig;

    public RpcServerAutoConfiguration(RpcConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
    }

    @Bean
    public SpringBeanResponseHandler springBeanResponseHandler() {
        return new SpringBeanResponseHandler();
    }

    @Bean
    public Server server() {
        Server server;
        if (Registry.DEFAULT.equals(rpcConfig.getRegistery())) {
            server = serverWithoutregistry();
        } else if (Registry.CONSUL.equals(rpcConfig.getRegistery())) {
            server = serverWithConsul();
        } else if (Registry.ZOOKEEPER.equals(rpcConfig.getRegistery())) {
            //TODO
            throw new IllegalArgumentException("don't support");
        } else {
            throw new IllegalArgumentException("properity Registry illega");
        }
        server.start();
        return server;
    }


    private Server serverWithoutregistry() {
        return new Server.Builder().port(rpcConfig.getPort())
                .maxCapacity(rpcConfig.getMaxCapacity())
                .responseHandler(springBeanResponseHandler())
                .build();
    }

    private Server serverWithConsul() {
        ServiceRegistry serviceRegistry = ConsulCompentFactory.getRegistry();
        return new Server.Builder().serviceRegistry(serviceRegistry)
                .serverId(rpcConfig.getServerId())
                .serverName(rpcConfig.getServerName())
                .ttl(rpcConfig.getTTL())
                .maxCapacity(rpcConfig.getMaxCapacity())
                .responseHandler(springBeanResponseHandler())
                .build();
    }

    private Server serverWithZookeeper() {
        return null;
    }


}
