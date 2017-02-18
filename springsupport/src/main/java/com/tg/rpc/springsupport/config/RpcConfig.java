package com.tg.rpc.springsupport.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by twogoods on 17/2/18.
 */
@Configuration(value = "rpcConfig")
@ConfigurationProperties(prefix = "tgrpc")
public class RpcConfig {

    private String host = "127.0.0.1";
    private int port = 9100;
    private int maxCapacity = 8;
    private int requestTimeoutMillis = 8000;

    private int maxTotal = 8;
    private int maxIdle = 8;
    private int minIdle = 0;
    private int borrowMaxWaitMillis = 8000;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getBorrowMaxWaitMillis() {
        return borrowMaxWaitMillis;
    }

    public void setBorrowMaxWaitMillis(int borrowMaxWaitMillis) {
        this.borrowMaxWaitMillis = borrowMaxWaitMillis;
    }

    @Override
    public String toString() {
        return "RpcConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", maxCapacity=" + maxCapacity +
                ", requestTimeoutMillis=" + requestTimeoutMillis +
                ", maxTotal=" + maxTotal +
                ", maxIdle=" + maxIdle +
                ", minIdle=" + minIdle +
                ", borrowMaxWaitMillis=" + borrowMaxWaitMillis +
                '}';
    }
}
