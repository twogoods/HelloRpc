package com.tg.rpc.springsupport.config;

import com.tg.rpc.consul.ConsulCompentFactory;
import com.tg.rpc.core.proxy.ClientProxy;
import com.tg.rpc.core.proxy.DefaultClientInterceptor;
import com.tg.rpc.core.bootstrap.Client;
import com.tg.rpc.core.proxy.CglibClientProxy;
import com.tg.rpc.core.proxy.MethodInterceptor;
import com.tg.rpc.core.servicecenter.Registery;
import com.tg.rpc.core.servicecenter.ServiceDiscovery;
import com.tg.rpc.springsupport.bean.client.RpcClientBeanPostProcessor;
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
        if (Registery.DEFAULT.equals(rpcConfig.getRegistery())) {
            client = new Client.Builder().host(rpcConfig.getHost())
                    .port(rpcConfig.getPort())
                    .maxCapacity(rpcConfig.getMaxCapacity())
                    .requestTimeoutMillis(rpcConfig.getRequestTimeoutMillis())
                    .connectionMaxTotal(rpcConfig.getMaxTotal())
                    .connectionMaxIdle(rpcConfig.getMaxIdle())
                    .connectionMinIdle(rpcConfig.getMinIdle())
                    .connectionBorrowMaxWaitMillis(rpcConfig.getBorrowMaxWaitMillis())
                    .build();
        } else if (Registery.CONSUL.equals(rpcConfig.getRegistery())) {
            ServiceDiscovery serviceDiscovery = ConsulCompentFactory.getDiscovery();
            client = new Client.Builder().serviceDiscovery(serviceDiscovery)
                    .serverName(rpcConfig.getServerName())
                    .maxCapacity(rpcConfig.getMaxCapacity())
                    .requestTimeoutMillis(rpcConfig.getRequestTimeoutMillis())
                    .connectionMaxTotal(rpcConfig.getMaxTotal())
                    .connectionMaxIdle(rpcConfig.getMaxIdle())
                    .connectionMinIdle(rpcConfig.getMinIdle())
                    .connectionBorrowMaxWaitMillis(rpcConfig.getBorrowMaxWaitMillis())
                    .build();
        } else if (Registery.ZOOKEEPER.equals(rpcConfig.getRegistery())) {
            throw new IllegalArgumentException("don't support");
        } else {
            throw new IllegalArgumentException("properity Registery illega");
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
}
