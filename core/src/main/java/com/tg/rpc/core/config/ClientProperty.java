package com.tg.rpc.core.config;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * @author twogoods
 * @since 2017/7/10
 */
@ToString
@Data
public class ClientProperty {
    private String serviceName;
    private int requestTimeoutMillis = 2000;
    private List<String> interfaces = new ArrayList<>();
    private List<String> providerList = new ArrayList<>();

    public ClientProperty serviceName(String serviceName) {
        this.serviceName = serviceName;
        return this;
    }

    public ClientProperty requestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
        return this;
    }

    public ClientProperty interfaces(String interfaces) {
        this.interfaces.add(interfaces);
        return this;
    }

    public ClientProperty provider(String provider) {
        this.providerList.add(provider);
        return this;
    }
}
