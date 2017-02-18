package com.tg.rpc.core.bootstrap;

import com.tg.rpc.core.codec.ProtocolDecoder;
import com.tg.rpc.core.codec.ProtocolEncoder;
import com.tg.rpc.core.entity.QueueHolder;
import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.handler.channel.ClientChannelHandler;
import com.tg.rpc.core.pool.ChannelPoolWrapper;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Method;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by twogoods on 17/2/16.
 */
public class Client {
    private static Logger log = LogManager.getLogger(Client.class);

    private String host;
    private int port;
    private int maxCapacity;
    private int requestTimeoutMillis = 10;

    private ChannelPoolWrapper channelPoolWrapper;

    private AtomicLong atomicLong = new AtomicLong(100000);

    public Client(String host, int port, int maxCapacity) {
        this.host = host;
        this.port = port;
        this.maxCapacity = maxCapacity;
    }

    public static class Builder {
        private String host;
        private int port;
        private int maxCapacity;

        public Client.Builder host(String host) {
            if (StringUtils.isEmpty(host)) {
                throw new NullPointerException("host can't be null");
            }
            this.host = host;
            return this;
        }

        public Client.Builder port(int port) {
            if (port <= 0) {
                throw new IllegalArgumentException("port can't be negative");
            }
            this.port = port;
            return this;
        }

        public Client.Builder maxCapacity(int maxCapacity) {
            if (maxCapacity <= 0) {
                throw new IllegalArgumentException("maxCapacity can't be negative");
            }
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Client build() {
            Validate.notEmpty(host, "host can't be empty");
            Validate.isTrue(port > 0, "port can't be negative, port:%d", port);
            Validate.isTrue(maxCapacity > 0, "maxCapacity can't be negative, maxCapacity:%d", maxCapacity);
            Client client = new Client(host, port, maxCapacity);
            client.initChannelPool();
            return client;
        }
    }

    public Channel initConnection() {
        try {
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ProtocolDecoder(maxCapacity * 1024 * 1024))
                                    .addLast(new ProtocolEncoder())
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ClientChannelHandler());
                        }
                    });

            //TODO 复用问题 http://www.cnblogs.com/kaiblog/p/5372728.html
            ChannelFuture f = b.connect(host, port).sync();

            f.addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        log.info("Connect to {}:{} Connect success! channel:{}", host, port, future.channel());
                    }
                }
            });
            final Channel channel = f.channel();
            channel.closeFuture().addListener(new ChannelFutureListener() {
                public void operationComplete(ChannelFuture future) throws Exception {
                    log.info("Channel Closed ", future.channel());
                }
            });
            return channel;
        } catch (Exception e) {
            log.error("client connect to server failed!", e);
        }
        return null;
    }

    public void initChannelPool() {
        channelPoolWrapper = new ChannelPoolWrapper(this);
    }

    public Response sendRequest(Method method, Object[] args, Class clazz, String serviceName) throws Exception {
        Request request = new Request(clazz, method.getName(), method.getParameterTypes(), args, serviceName);
        request.setRequestId(atomicLong.incrementAndGet());
        Channel channel = channelPoolWrapper.getObject();
        try {
            channel.writeAndFlush(request);
            BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue(1);
            QueueHolder.put(request.getRequestId(), blockingQueue);
            Response response = blockingQueue.poll(requestTimeoutMillis, TimeUnit.SECONDS);
            return response;
        } finally {
            channelPoolWrapper.returnObject(channel);
            QueueHolder.remove(request.getRequestId());
        }
    }
}
