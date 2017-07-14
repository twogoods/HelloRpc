package com.tg.rpc.core.servicecenter;

import lombok.Data;
import lombok.ToString;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-28
 */
@Data
@ToString
public class Service {
    private String id;
    private String name;
    private String address;
    private Integer port;
    private long ttl;
}
