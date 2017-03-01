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
    public static final String DEFAULT_SERVICE_NAME = "default_rpc";
    public static final String DEFAULT_SERVICE_ID = "default_service";
    public static final long DEFAULT_TTL = 5000;


    public static final String DEFAULT_HOST = "localhost";
    public static final int DEFAULT_PORT = 9001;
    public static final int DEFAULT_MAXCAPACITY = 8;
    public static final int DEFAULT_REQUESTIMEOUTMILLIS = 8000;

    public static final int DEFAULT_POOL_MAXTOTAL = 8;
    public static final int DEFAULT_POOL_MAXIDLE = 8;
    public static final int DEFAULT_POOL_MINIDLE = 8;
    public static final int DEFAULT_POOL_BORROWMAXWAITMILLIS = 8000;

}
