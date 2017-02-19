package com.tg.rpc.example.springsupport.server;

import com.tg.rpc.core.bootstrap.Server;
import com.tg.rpc.springsupport.bean.server.SpringBeanResponseHandler;
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
@ComponentScan(value = {"com.tg.rpc.springsupport.bean.server","com.tg.rpc.springsupport.config",
        "com.tg.rpc.example.springsupport.service"})
public class ServerApplication {

    @Bean
    public SpringBeanResponseHandler springBeanResponseHandler() {
        return new SpringBeanResponseHandler();
    }

    @Bean
    public Server server(@Qualifier("rpcConfig") RpcConfig rpcConfig) {
        Server server = new Server.Builder().port(rpcConfig.getPort())
                .maxCapacity(rpcConfig.getMaxCapacity())
                .responseHandler(springBeanResponseHandler())
                .build();
        server.start();
        return server;
    }


    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ServerApplication.class);
    }
}
