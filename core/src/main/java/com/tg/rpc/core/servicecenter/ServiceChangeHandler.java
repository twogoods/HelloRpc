package com.tg.rpc.core.servicecenter;

import java.util.List;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-28
 */
public interface ServiceChangeHandler {

    void handle(List<Service> services);
}
