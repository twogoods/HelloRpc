package com.tg.rpc.springsupport.config;

import com.tg.rpc.core.entity.ConfigConstant;
import com.tg.rpc.core.servicecenter.Registry;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by twogoods on 17/2/18.
 */
@Data
@ConfigurationProperties(prefix = RpcConfig.TGRPC_PREFIX)
public class RpcConfig {

    public static final String TGRPC_PREFIX = "tgrpc";

    private String host = ConfigConstant.DEFAULT_HOST;
    private int port = ConfigConstant.DEFAULT_PORT;
    private int maxCapacity = ConfigConstant.DEFAULT_MAXCAPACITY;
    private int requestTimeoutMillis = ConfigConstant.DEFAULT_REQUESTIMEOUTMILLIS;

    private int maxTotal = ConfigConstant.DEFAULT_POOL_MAXTOTAL;
    private int maxIdle = ConfigConstant.DEFAULT_POOL_MAXIDLE;
    private int minIdle = ConfigConstant.DEFAULT_POOL_MINIDLE;
    private int borrowMaxWaitMillis = ConfigConstant.DEFAULT_POOL_BORROWMAXWAITMILLIS;

    private String consulHost = ConfigConstant.DEFAULT_CONSUL_HOST;
    private int consulPort = ConfigConstant.DEFAULT_CONSUL_PORT;
    private long ttl = ConfigConstant.DEFAULT_TTL;
    private String zookeeperHost = ConfigConstant.DEFAULT_ZOOKEEPER_HOST;
    private int zookeeperPort = ConfigConstant.DEFAULT_ZOOKEEPER_PORT;
    private String zkServicePath = ConfigConstant.DEFAULT_ZOOKEEPER_SERVICE_PATH;
    private String serverName;
    private String serverId;

    private String registery = Registry.DEFAULT.value();
}
