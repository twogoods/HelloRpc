package com.tg.rpc.example.springsupport.client;

import com.tg.rpc.springsupport.bean.client.RpcClientBeanPostProcessor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Created by twogoods on 17/2/18.
 */
@SpringBootApplication
@EnableAutoConfiguration
//@ComponentScan(basePackages = {"com.tg.rpc.example.springsupport"})
public class ClientApplication {

    @Bean
    public RpcClientBeanPostProcessor rpcClientBeanPostProcessor() {
        return new RpcClientBeanPostProcessor();
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ClientApplication.class);

    }
}
