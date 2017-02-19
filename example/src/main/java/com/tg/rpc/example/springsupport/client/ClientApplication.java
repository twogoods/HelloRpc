package com.tg.rpc.example.springsupport.client;

import com.tg.rpc.core.bootstrap.Client;
import com.tg.rpc.core.proxy.CglibClientProxy;
import com.tg.rpc.core.proxy.ClientProxy;
import com.tg.rpc.core.proxy.MethodInterceptor;
import com.tg.rpc.core.proxy.DefaultClientInterceptor;
import com.tg.rpc.springsupport.bean.client.RpcClientBeanPostProcessor;
import com.tg.rpc.springsupport.config.RpcConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by twogoods on 17/2/18.
 */
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.tg.rpc.example.springsupport.service", "com.tg.rpc.example.springsupport.client",
        "com.tg.rpc.springsupport.bean.client",
        "com.tg.rpc.springsupport.config"})
public class ClientApplication {

    //TODO 简化Bean的配置以及ComponentScan扫描包的配置

    @Bean("defaultClient")
    public Client client(@Qualifier("rpcConfig") RpcConfig rpcConfig) {
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

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ClientApplication.class);
        ServiceCall serviceCall = (ServiceCall) applicationContext.getBean("serviceCall");
        System.out.println(serviceCall.call());
    }
}
