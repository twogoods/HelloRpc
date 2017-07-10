package com.tg.rpc.core.config;

import javax.print.DocFlavor;
import java.util.Arrays;
import java.util.List;

/**
 * @author twogoods
 * @since 2017/7/10
 */
public class ClientProperty {
    private String name;
    private int requestTimeoutMillis = 2000;
    private String interfaces;
    private String providerList;
    private String registery;
    private int maxCapacity = 3;
    private int maxTotal = 3;
    private int maxIdle = 3;
    private int minIdle = 1;
    private int borrowMaxWaitMillis = 5000;

    public ClientProperty setName(String name) {
        this.name = name;
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

    public ClientProperty setRegistery(String registery) {
        this.registery = registery;
        return this;
    }

    public ClientProperty setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        return this;
    }

    public ClientProperty setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
        return this;
    }

    public ClientProperty setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
        return this;
    }

    public ClientProperty setMinIdle(int minIdle) {
        this.minIdle = minIdle;
        return this;
    }

    public ClientProperty setBorrowMaxWaitMillis(int borrowMaxWaitMillis) {
        this.borrowMaxWaitMillis = borrowMaxWaitMillis;
        return this;
    }

    public String getName() {
        return name;
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

    public String getRegistery() {
        return registery;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public int getBorrowMaxWaitMillis() {
        return borrowMaxWaitMillis;
    }
}
