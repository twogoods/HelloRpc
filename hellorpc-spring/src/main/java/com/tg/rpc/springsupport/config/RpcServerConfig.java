package com.tg.rpc.springsupport.config;

import com.tg.rpc.core.entity.ConfigConstant;
import com.tg.rpc.core.servicecenter.Registry;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by twogoods on 17/2/18.
 */
@Data
@ToString
@ConfigurationProperties(prefix = RpcServerConfig.TGRPC_PREFIX)
public class RpcServerConfig {

    public static final String TGRPC_PREFIX = "tgrpc.server";

    private int port = ConfigConstant.DEFAULT_PORT;
    private int maxCapacity = ConfigConstant.DEFAULT_MAXCAPACITY;
    private int requestTimeoutMillis = ConfigConstant.DEFAULT_REQUESTIMEOUTMILLIS;

    private String consulHost = ConfigConstant.DEFAULT_CONSUL_HOST;
    private int consulPort = ConfigConstant.DEFAULT_CONSUL_PORT;
    private long ttl = ConfigConstant.DEFAULT_TTL;
    private String zookeeperHost = ConfigConstant.DEFAULT_ZOOKEEPER_HOST;
    private int zookeeperPort = ConfigConstant.DEFAULT_ZOOKEEPER_PORT;
    private String zkServicePath = ConfigConstant.DEFAULT_ZOOKEEPER_SERVICE_PATH;
    private String serviceName;
    private String serviceId;

    private Registry registery = Registry.DEFAULT;
}
