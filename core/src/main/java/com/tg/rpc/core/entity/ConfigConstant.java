package com.tg.rpc.core.entity;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-03-01
 */
public class ConfigConstant {
    public static final String DEFAULT_CONSUL_HOST = "localhost";
    public static final int DEFAULT_CONSUL_PORT = 8500;
    public static final long DEFAULT_CONSUL_LOOKUPINTERVAL = 8000;
    public static final long DEFAULT_TTL = 30000;
    public static final String DEFAULT_ZOOKEEPER_HOST = "localhost";
    public static final int DEFAULT_ZOOKEEPER_PORT = 2181;
    public static final String DEFAULT_ZOOKEEPER_SERVICE_PATH = "/tgrpc/services";


    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 9001;
    public static final int DEFAULT_MAXCAPACITY = 8;
    public static final int DEFAULT_REQUESTIMEOUTMILLIS = 8000;

    public static final int DEFAULT_POOL_MAXTOTAL = 20;
    public static final int DEFAULT_POOL_MAXIDLE = 16;
    public static final int DEFAULT_POOL_MINIDLE = 5;
    public static final int DEFAULT_POOL_BORROWMAXWAITMILLIS = 8000;

}
