package com.tg.rpc.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.agent.model.NewService;

import java.util.Arrays;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-07-13
 */
public class ConsulRegistryTest {
    public void testRegister() throws Exception {
        ConsulClient client = new ConsulClient("localhost");

        NewService newService = new NewService();
        newService.setId("hello");
        newService.setName("helloservice");
        newService.setTags(Arrays.asList("0.1"));
        newService.setPort(8080);
        client.agentServiceRegister(newService);

    }
}