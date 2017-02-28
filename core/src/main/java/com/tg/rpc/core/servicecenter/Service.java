package com.tg.rpc.core.servicecenter;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-28
 */
public class Service {
    private String id;
    private String name;
    private String address;
    private Integer port;
    private long ttl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", port=" + port +
                ", ttl=" + ttl +
                '}';
    }
}
