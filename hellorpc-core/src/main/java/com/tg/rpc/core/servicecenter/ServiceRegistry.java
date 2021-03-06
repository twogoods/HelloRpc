package com.tg.rpc.core.servicecenter;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-28
 */
public interface ServiceRegistry {

    void register(Service service) throws Exception;

    void unregister(Service service) throws Exception;

    long getTTL();

    void close();
}
