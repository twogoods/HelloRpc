package com.tg.rpc.core.bootstrap;

import com.tg.rpc.core.codec.ProtocolDecoder;
import com.tg.rpc.core.codec.ProtocolEncoder;
import com.tg.rpc.core.entity.QueueHolder;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.pool.ChannelPoolWrapper;
import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.handler.channel.ClientChannelHandler;
import com.tg.rpc.core.servicecenter.Service;
import com.tg.rpc.core.servicecenter.ServiceChangeHandler;
import com.tg.rpc.core.servicecenter.ServiceDiscovery;
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
import org.apache.logging.log4j.spi.CopyOnWrite;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by twogoods on 17/2/16.
 */
public class Client {
    private static Logger log = LogManager.getLogger(Client.class);

    private String host;
    private int port;
    private ServiceDiscovery serviceDiscovery;
    private int maxCapacity;
    private int requestTimeoutMillis;
    private String serverName;

    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private int borrowMaxWaitMillis;

    private CopyOnWriteArrayList<ChannelPoolWrapper> channelPoolWrappers = new CopyOnWriteArrayList();

    private AtomicLong atomicLong = new AtomicLong(100000);

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Client(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public static class Builder {
        private String host;
        private int port;
        private ServiceDiscovery serviceDiscovery;
        private int maxCapacity = 8;
        private int requestTimeoutMillis = 8000;
        private String serverName = "default_rpc";

        private int maxTotal = 8;
        private int maxIdle = 8;
        private int minIdle = 0;
        private int borrowMaxWaitMillis = 8000;

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
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Client.Builder serverName(String serverName) {
            this.serverName = serverName;
            return this;
        }

        public Client.Builder requestTimeoutMillis(int requestTimeoutMillis) {
            this.requestTimeoutMillis = requestTimeoutMillis;
            return this;
        }

        public Client.Builder connectionMaxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
            return this;
        }

        public Client.Builder connectionMaxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
            return this;
        }

        public Client.Builder connectionMinIdle(int minIdle) {
            this.minIdle = minIdle;
            return this;
        }

        public Client.Builder connectionBorrowMaxWaitMillis(int borrowMaxWaitMillis) {
            this.borrowMaxWaitMillis = borrowMaxWaitMillis;
            return this;
        }

        public Client build() {
            Validate.isTrue(maxCapacity > 0, "maxCapacity must bigger than zero, maxCapacity:%d", maxCapacity);
            Validate.isTrue(requestTimeoutMillis > 0, "maxCapacity must bigger than zero, maxCapacity:%d", requestTimeoutMillis);
            Validate.isTrue(maxTotal > 0, "maxTotal must bigger than zero, maxCapacity:%d", maxTotal);
            Validate.isTrue(maxIdle > 0, "maxIdle must bigger than zero, maxCapacity:%d", maxIdle);
            Validate.isTrue(minIdle >= 0, "minIdle can't be negative, maxCapacity:%d", minIdle);
            Validate.isTrue(borrowMaxWaitMillis > 0, "borrowMaxWaitMillis must bigger than zero, maxCapacity:%d", borrowMaxWaitMillis);

            Client client;
            if (StringUtils.isEmpty(host)) {
                Validate.notNull(serviceDiscovery, "no host and port, so serviceDiscovery can't be null");
                client = new Client(serviceDiscovery);
            } else {
                Validate.isTrue(port > 0, "port can't be negative, port:%d", port);
                client = new Client(host, port);
            }
            client.maxCapacity = maxCapacity;
            client.requestTimeoutMillis = this.requestTimeoutMillis;
            client.serverName = serverName;
            client.maxTotal = this.maxTotal;
            client.maxIdle = this.maxIdle;
            client.minIdle = this.minIdle;
            client.borrowMaxWaitMillis = this.borrowMaxWaitMillis;
            client.init();
            return client;
        }
    }

    public Channel initConnection(String host, int port) {
        try {
            EventLoopGroup group = new NioEventLoopGroup();
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, borrowMaxWaitMillis)
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
            log.info("client try to connection {}:{}", host, port);
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

    private void init() {
        if (serviceDiscovery != null) {
            List<Service> serviceList = serviceDiscovery.discover(serverName);
            for (Service service : serviceList) {
                addChannel(service.getAddress(), service.getPort());
            }
        } else {
            addChannel(host, port);
        }

        serviceDiscovery.addListener(serverName, new ServiceChangeHandler() {
            @Override
            public void handle(List<Service> services) {

                //去除
                for (ChannelPoolWrapper channelPoolWrapper : channelPoolWrappers) {
                    boolean channelcontain = false;
                    for (Service service : services) {
                        if (isServerSame(channelPoolWrapper, service)) {
                            channelcontain = true;
                            break;
                        }
                    }
                    if(!channelcontain){
                        channelPoolWrappers.remove(channelPoolWrapper);
                    }
                }

                //增加新的
                for (Service service : services) {
                    boolean contain = false;
                    for (ChannelPoolWrapper channelPoolWrapper : channelPoolWrappers) {
                        if (isServerSame(channelPoolWrapper, service)) {
                            contain = true;
                            break;
                        }
                    }
                    if (!contain) {
                        addChannel(service.getAddress(), service.getPort());
                    }
                }

            }
        });

        log.info("connect...");

        //TODO 通过注册中心拿服务

    }

    private boolean isServerSame(ChannelPoolWrapper channelPoolWrapper, Service service) {
        return channelPoolWrapper.getHost().equals(service.getAddress()) && channelPoolWrapper.getPort() == service.getPort();
    }

    private void addChannel(String host, int port) {
        channelPoolWrappers.add(new ChannelPoolWrapper(this, host, port));
    }


    private ChannelPoolWrapper selectChannel() {
        //TODO
        return null;
    }

    public Response sendRequest(Method method, Object[] args, Class clazz, String serviceName) throws Exception {
        Request request = new Request(clazz, method.getName(), method.getParameterTypes(), args, serviceName);
        request.setRequestId(atomicLong.incrementAndGet());
        ChannelPoolWrapper channelPoolWrapper = selectChannel();
        Channel channel = channelPoolWrapper.getObject();
        if (channel == null) {
            Validate.notNull(channel, "can't get channel from pool");
        }
        try {
            channel.writeAndFlush(request);
            BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue(1);
            QueueHolder.put(request.getRequestId(), blockingQueue);
            Response response = blockingQueue.poll(requestTimeoutMillis, TimeUnit.MILLISECONDS);
            return response;
        } finally {
            channelPoolWrapper.returnObject(channel);
            QueueHolder.remove(request.getRequestId());
        }
    }

    public int getRequestTimeoutMillis() {
        return requestTimeoutMillis;
    }

    public void setRequestTimeoutMillis(int requestTimeoutMillis) {
        this.requestTimeoutMillis = requestTimeoutMillis;
    }

    public int getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(int maxTotal) {
        this.maxTotal = maxTotal;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    public int getBorrowMaxWaitMillis() {
        return borrowMaxWaitMillis;
    }

    public void setBorrowMaxWaitMillis(int borrowMaxWaitMillis) {
        this.borrowMaxWaitMillis = borrowMaxWaitMillis;
    }
}
