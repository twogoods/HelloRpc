package com.tg.rpc.springsupport.config;

import com.tg.rpc.core.bootstrap.Server;
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
        Server server = new Server.Builder().port(rpcConfig.getPort())
                .maxCapacity(rpcConfig.getMaxCapacity())
                .responseHandler(springBeanResponseHandler())
                .build();
        server.start();
        return server;
    }

}
