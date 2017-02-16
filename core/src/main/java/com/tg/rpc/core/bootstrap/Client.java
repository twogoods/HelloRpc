package com.tg.rpc.core.bootstrap;

import com.tg.rpc.core.exception.ValidateException;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by twogoods on 17/2/16.
 */
public class Client {
    private String host;
    private int port;
    private int maxCapacity;

    public Client(String host, int port, int maxCapacity) {
        this.host = host;
        this.port = port;
        this.maxCapacity = maxCapacity;
    }

    public static class Builder {
        private String host;
        private int port;
        private int maxCapacity;

        public Client.Builder host(String host) throws ValidateException {
            if (StringUtils.isEmpty(host)) {
                throw new ValidateException("host can't be null");
            }
            this.host = host;
            return this;
        }

        public Client.Builder port(int port) throws ValidateException {
            if (port <= 0) {
                throw new ValidateException("port can't be negative");
            }
            this.port = port;
            return this;
        }

        public Client.Builder maxCapacity(int maxCapacity) throws ValidateException {
            if (maxCapacity <= 0) {
                throw new ValidateException("maxCapacity can't be negative");
            }
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Client build() {
            return new Client(host, port, maxCapacity);
        }
    }
}
