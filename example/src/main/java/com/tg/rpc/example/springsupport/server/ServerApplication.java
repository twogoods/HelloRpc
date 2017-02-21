package com.tg.rpc.example.springsupport.server;

import com.tg.rpc.springsupport.annotation.EnableRpcServer;
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
@EnableRpcServer
@ComponentScan(value = {"com.tg.rpc.example.springsupport.service"})
public class ServerApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ServerApplication.class);
    }
}
