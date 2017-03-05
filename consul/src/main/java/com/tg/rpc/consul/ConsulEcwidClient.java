package com.tg.rpc.consul;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.ConsulRawClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.health.model.HealthService;
import com.tg.rpc.core.servicecenter.Service;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-28
 */
public class ConsulEcwidClient {

    private static final int blockTime = 5 * 60;
    private ConsulClient consulClient;

    private Map<String, Long> lastConsulIndexMap = new ConcurrentHashMap<>();

    public ConsulEcwidClient(String host, int port) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(1000);
        connectionManager.setDefaultMaxPerRoute(500);
        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000).setSocketTimeout(blockTime * 2 * 1000).build();
        HttpClient httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).setDefaultRequestConfig(requestConfig).useSystemProperties().build();
        ConsulRawClient consulRawClient = new ConsulRawClient(host, port, httpClient);
        this.consulClient = new ConsulClient(consulRawClient);
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
        Long lastindex = lastConsulIndexMap.get(serviceName);
        if (lastindex == null) {
            lastindex = 0L;
        }
        QueryParams queryParams = new QueryParams(blockTime, lastindex);
        Response<List<HealthService>> response = consulClient.getHealthServices(serviceName, true, queryParams);
        List<Service> list = new ArrayList<>(response.getValue().size());
        for (HealthService healthService : response.getValue()) {
            list.add(convertToService(healthService));
        }
        lastConsulIndexMap.put(serviceName, response.getConsulIndex());
        return list;
    }

    private NewService convertToConsulService(Service service) {
        NewService newService = new NewService();
        newService.setId(service.getId());
        newService.setName(service.getName());
        newService.setAddress(service.getAddress());
        newService.setPort(service.getPort());
        NewService.Check serviceCheck = new NewService.Check();
        serviceCheck.setTtl(service.getTtl() / 1000 + "s");
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
}
