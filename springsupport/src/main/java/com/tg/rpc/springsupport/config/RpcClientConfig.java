package com.tg.rpc.springsupport.config;

import com.tg.rpc.core.entity.ConfigConstant;
import com.tg.rpc.core.servicecenter.Registry;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by twogoods on 17/2/18.
 */
@ConfigurationProperties(prefix = RpcClientConfig.TGRPC_PREFIX)
public class RpcClientConfig {

    public static final String TGRPC_PREFIX = "tgrpc.client";

    private String host = ConfigConstant.DEFAULT_HOST;
    private int port = ConfigConstant.DEFAULT_PORT;
    private int maxCapacity = ConfigConstant.DEFAULT_MAXCAPACITY;
    private int requestTimeoutMillis = ConfigConstant.DEFAULT_REQUESTIMEOUTMILLIS;

    private int maxTotal = ConfigConstant.DEFAULT_POOL_MAXTOTAL;
    private int maxIdle = ConfigConstant.DEFAULT_POOL_MAXIDLE;
    private int minIdle = ConfigConstant.DEFAULT_POOL_MINIDLE;
    private int borrowMaxWaitMillis = ConfigConstant.DEFAULT_POOL_BORROWMAXWAITMILLIS;

    private String consulHost = ConfigConstant.DEFAULT_REGISTRY_HOST;
    private int consulPort = ConfigConstant.DEFAULT_REGISTRY_PORT;
    private long ttl = ConfigConstant.DEFAULT_TTL;
    private String serverName = ConfigConstant.DEFAULT_SERVICE_NAME;
    private String serverId = ConfigConstant.DEFAULT_SERVICE_ID;
    private String zookeeperHost = ConfigConstant.DEFAULT_ZOOKEEPER_HOST;
    private int zookeeperPort = ConfigConstant.DEFAULT_ZOOKEEPER_PORT;
    private String zkServicePath = ConfigConstant.DEFAULT_ZOOKEEPER_SERVICE_PATH;

    private String registery = Registry.DEFAULT.value();

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

    public String getConsulHost() {
        return consulHost;
    }

    public void setConsulHost(String consulHost) {
        this.consulHost = consulHost;
    }

    public int getConsulPort() {
        return consulPort;
    }

    public void setConsulPort(int consulPort) {
        this.consulPort = consulPort;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public long getTTL() {
        return ttl;
    }

    public void setTTL(long ttl) {
        this.ttl = ttl;
    }

    public String getRegistery() {
        return registery;
    }

    public void setRegistery(String registery) {
        this.registery = registery;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public String getZkServicePath() {
        return zkServicePath;
    }

    public void setZkServicePath(String zkServicePath) {
        this.zkServicePath = zkServicePath;
    }

    public String getZookeeperHost() {
        return zookeeperHost;
    }

    public void setZookeeperHost(String zookeeperHost) {
        this.zookeeperHost = zookeeperHost;
    }

    public int getZookeeperPort() {
        return zookeeperPort;
    }

    public void setZookeeperPort(int zookeeperPort) {
        this.zookeeperPort = zookeeperPort;
    }
}
