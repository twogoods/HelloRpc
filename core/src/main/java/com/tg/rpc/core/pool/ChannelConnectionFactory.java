package com.tg.rpc.core.pool;

import com.tg.rpc.core.codec.ProtocolDecoder;
import com.tg.rpc.core.codec.ProtocolEncoder;
import com.tg.rpc.core.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by twogoods on 17/2/16.
 */
public class ChannelConnectionFactory extends BasePooledObjectFactory<Channel> {

    private static Logger log = LogManager.getLogger(ChannelConnectionFactory.class);

    private String host;
    private int port;

    public ChannelConnectionFactory() {
    }

    public ChannelConnectionFactory(String host, int port) {
        this.host = host;
        this.port = port;
    }

    //TODO 初始化代码转移到client
    private Channel initConnection() {
        try {
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {

                            ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO))
                                    .addLast(new ProtocolDecoder(10 * 1024 * 1024))
                                    .addLast(new ProtocolEncoder())
                                    .addLast(new ClientHandler());
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("Connect to {}:{} Connect success!", host, port);
                    }
                }
            });
            final Channel channel = f.channel();
            channel.closeFuture().addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    log.info("Channel Closed {}:{} ", host, port);
                }
            }).sync();
            return channel;
        } catch (Exception e) {
            log.error("client connect to server failed!", e);
        }
        return null;
    }


    @Override
    public Channel create() throws Exception {
        return initConnection();
    }

    @Override
    public PooledObject<Channel> wrap(Channel channel) {
        return new DefaultPooledObject<Channel>(channel);
    }

    @Override
    public void destroyObject(PooledObject<Channel> p) throws Exception {
        p.getObject().close();
    }

    @Override
    public boolean validateObject(PooledObject<Channel> p) {
        return p.getObject().isActive();
    }
}
