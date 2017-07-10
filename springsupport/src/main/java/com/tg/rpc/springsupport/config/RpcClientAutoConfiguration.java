package com.tg.rpc.springsupport.config;

import com.tg.rpc.consul.ConsulCompentFactory;
import com.tg.rpc.core.proxy.ClientProxy;
import com.tg.rpc.core.proxy.DefaultClientInterceptor;
import com.tg.rpc.core.bootstrap.Client;
import com.tg.rpc.core.proxy.CglibClientProxy;
import com.tg.rpc.core.proxy.MethodInterceptor;
import com.tg.rpc.core.servicecenter.Registry;
import com.tg.rpc.core.servicecenter.ServiceDiscovery;
import com.tg.rpc.springsupport.bean.client.RpcClientBeanPostProcessor;
import com.tg.rpc.zookeeper.ZookeeperCompentFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by twogoods on 17/2/21.
 */
@Configuration
@EnableConfigurationProperties(RpcConfig.class)
public class RpcClientAutoConfiguration {

    private final RpcConfig rpcConfig;

    public RpcClientAutoConfiguration(RpcConfig rpcConfig) {
        this.rpcConfig = rpcConfig;
    }

    @Bean("defaultClient")
    public Client client() {
        Client client;
        if (Registry.DEFAULT.equals(rpcConfig.getRegistery())) {
            client = clientWithoutregistry();
        } else if (Registry.CONSUL.equals(rpcConfig.getRegistery())) {
            client = clientWithConsul();
        } else if (Registry.ZOOKEEPER.equals(rpcConfig.getRegistery())) {
            client = clientWithZookeeper();
        } else {
            throw new IllegalArgumentException("properity Registry illega");
        }
        return client;
    }

    @Bean("defaultRpcClientInterceptor")
    public MethodInterceptor rpcClientInterceptor(@Qualifier("defaultClient") Client client) {
        return new DefaultClientInterceptor(client);
    }

    @Bean("cglibClientProxy")
    public ClientProxy cglibClientProxy(@Qualifier("defaultRpcClientInterceptor") MethodInterceptor rpcClientInterceptor) {
        return new CglibClientProxy(rpcClientInterceptor);
    }

    @Bean
    public RpcClientBeanPostProcessor rpcClientBeanPostProcessor(@Qualifier("cglibClientProxy") ClientProxy cglibClientProxy) {
        return new RpcClientBeanPostProcessor(cglibClientProxy);
    }

    private Client clientWithoutregistry() {
        return new Client.Builder().host(rpcConfig.getHost())
                .port(rpcConfig.getPort())
                .maxCapacity(rpcConfig.getMaxCapacity())
                .requestTimeoutMillis(rpcConfig.getRequestTimeoutMillis())
                .connectionMaxTotal(rpcConfig.getMaxTotal())
                .connectionMaxIdle(rpcConfig.getMaxIdle())
                .connectionMinIdle(rpcConfig.getMinIdle())
                .connectionBorrowMaxWaitMillis(rpcConfig.getBorrowMaxWaitMillis())
                .build();
    }

    private Client clientWithConsul() {
        ServiceDiscovery serviceDiscovery = ConsulCompentFactory.getDiscovery(rpcConfig.getConsulHost(), rpcConfig.getConsulPort());
        return new Client.Builder().serviceDiscovery(serviceDiscovery)
                .maxCapacity(rpcConfig.getMaxCapacity())
                .requestTimeoutMillis(rpcConfig.getRequestTimeoutMillis())
                .connectionMaxTotal(rpcConfig.getMaxTotal())
                .connectionMaxIdle(rpcConfig.getMaxIdle())
                .connectionMinIdle(rpcConfig.getMinIdle())
                .connectionBorrowMaxWaitMillis(rpcConfig.getBorrowMaxWaitMillis())
                .build();
    }

    private Client clientWithZookeeper() {
        ServiceDiscovery serviceDiscovery = ZookeeperCompentFactory.getDiscovery(rpcConfig.getZookeeperHost(), rpcConfig.getZookeeperPort(), rpcConfig.getZkServicePath());
        return new Client.Builder().serviceDiscovery(serviceDiscovery)
                .maxCapacity(rpcConfig.getMaxCapacity())
                .requestTimeoutMillis(rpcConfig.getRequestTimeoutMillis())
                .connectionMaxTotal(rpcConfig.getMaxTotal())
                .connectionMaxIdle(rpcConfig.getMaxIdle())
                .connectionMinIdle(rpcConfig.getMinIdle())
                .connectionBorrowMaxWaitMillis(rpcConfig.getBorrowMaxWaitMillis())
                .build();
    }
}
