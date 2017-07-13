package com.tg.rpc.core.config;

import java.util.Arrays;
import java.util.List;

/**
 * @author twogoods
 * @since 2017/7/10
 */
public class ClientProperty {
    private String serviceName;
    private int requestTimeoutMillis = 2000;
    private String interfaces;
    private String providerList;

    public ClientProperty setName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public ClientProperty setRequestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
        return this;
    }

    public ClientProperty setInterfaces(String interfaces) {
        this.interfaces = interfaces;
        return this;
    }

    public ClientProperty setProviderList(String providerList) {
        this.providerList = providerList;
        return this;
    }

    public String getServiceName() {
        return serviceName;
    }

    public int getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public List<String> getInterfaces() {
        return Arrays.asList(interfaces.split(","));
    }

    public List<String> getProviderList() {
        return Arrays.asList(providerList.split(","));
    }
}
