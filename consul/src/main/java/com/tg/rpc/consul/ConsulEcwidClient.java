package com.tg.rpc.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.model.HealthService;
import com.tg.rpc.core.servicecenter.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-28
 */
public class ConsulEcwidClient {
    private ConsulClient consulClient;

    public ConsulEcwidClient(String host, int port) {
        this.consulClient = new ConsulClient(host, port);
    }

    public void registerService(Service service) {
        NewService newService = convertToConsulService(service);
        consulClient.agentServiceRegister(newService);
    }

    public void unregisterService(String serviceid) {
        consulClient.agentServiceDeregister(serviceid);
    }

    public void checkPass(String serviceId) {
        consulClient.agentCheckPass("service:" + serviceId);
    }


    public List<Service> lookupHealthService(String serviceName) {
        Response<List<HealthService>> response = consulClient.getHealthServices(serviceName, true, QueryParams.DEFAULT);
        List<Service> list = new ArrayList<>(response.getValue().size());
        for (HealthService healthService : response.getValue()) {
            list.add(convertToService(healthService));
        }
        return list;
    }

    private NewService convertToConsulService(Service service) {
        NewService newService = new NewService();
        newService.setId(service.getId());
        newService.setName(service.getName());
        newService.setAddress(service.getAddress());
        newService.setPort(service.getPort());
        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setTtl(service.getTtl() + "s");
        newService.setCheck(serviceCheck);
        return newService;
    }

    private Service convertToService(HealthService healthService) {
        Service service = new Service();
        service.setId(healthService.getService().getId());
        service.setName(healthService.getService().getService());
        service.setAddress(healthService.getService().getAddress());
        service.setPort(healthService.getService().getPort());
        return service;
    }

    public static void main(String[] args) {
        ConsulEcwidClient client = new ConsulEcwidClient("127.0.0.1", 8500);
        System.out.println(client.lookupHealthService("book"));
    }

}
