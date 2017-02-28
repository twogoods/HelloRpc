package com.tg.rpc.consul;

import com.ecwid.consul.v1.ConsistencyMode;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.model.HealthService;

import java.util.List;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-21
 */
public class consul {
    public static void main(String[] args) throws InterruptedException {
        ConsulClient client = new ConsulClient("localhost");

        NewService newService = new NewService();
        newService.setId("kobe");
        newService.setName("book");
        newService.setAddress("127.0.0.1");
        newService.setPort(9001);
        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setTtl("200s");
        newService.setCheck(serviceCheck);
        client.agentServiceRegister(newService);


        //TTL型的check需要自己发送健康状态
        client.agentCheckPass("service:twogoods");

        Response<List<HealthService>> response = client.getHealthServices("book", true, new QueryParams(180L, 280));
        System.out.println(response.getConsulIndex());
        System.out.println(response.getConsulLastContact());
        System.out.println(response.isConsulKnownLeader());
        System.out.println(response.getValue());
    }
}
