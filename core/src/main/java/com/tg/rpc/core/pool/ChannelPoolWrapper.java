package com.tg.rpc.core.pool;

import com.tg.rpc.core.bootstrap.Client;
import io.netty.channel.Channel;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;


/**
 * Created by twogoods on 17/2/16.
 */
public class ChannelPoolWrapper {

    private long borrowMaxWaitMillis;

    private GenericObjectPool<Channel> pool;

    public ChannelPoolWrapper(Client client) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(client.getMaxTotal());
        config.setMaxIdle(client.getMaxIdle());
        config.setMinIdle(client.getMinIdle());
        this.borrowMaxWaitMillis = client.getBorrowMaxWaitMillis();
        pool = new GenericObjectPool<Channel>(new ChannelConnectionFactory(client), config);
    }

    public void close() {
        pool.close();
    }

    public Channel getObject() throws Exception {
        return pool.borrowObject(borrowMaxWaitMillis);
    }

    public void returnObject(Channel channel) {
        pool.returnObject(channel);
    }
}
