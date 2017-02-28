package com.tg.rpc.consul;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-22
 */
public class ConsulHeartbeatManger {
    private ConsulEcwidClient consulEcwidClient;

    private HashSet<String> serviceIds = new HashSet<String>();

    public ConsulHeartbeatManger(ConsulEcwidClient consulEcwidClient) {
        this.consulEcwidClient = consulEcwidClient;
    }

    public void addHeartbeatService(String serviceId){
        serviceIds.add(serviceId);
    }

    public void removeHeartbeatService(String serviceId){
        serviceIds.remove(serviceId);
    }






}
