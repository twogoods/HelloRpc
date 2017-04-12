package com.tg.rpc.example.springsupport.client;

import com.tg.rpc.springsupport.annotation.EnableRpcClient;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by twogoods on 17/2/18.
 */
@SpringBootApplication
@EnableAutoConfiguration
@EnableRpcClient
@ComponentScan(basePackages = {"com.tg.rpc.example.springsupport.client", "com.tg.rpc.example.service"})
public class ClientApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ClientApplication.class);
        ServiceCall serviceCall = (ServiceCall) applicationContext.getBean("serviceCall");
        System.out.println("echo return :" + serviceCall.echo("TgRPC"));
        System.out.println("4 + 5 = " + serviceCall.add(4, 5));
    }
}
