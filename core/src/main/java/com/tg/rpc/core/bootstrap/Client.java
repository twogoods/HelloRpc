package com.tg.rpc.core.bootstrap;

import com.tg.rpc.core.codec.ProtocolDecoder;
import com.tg.rpc.core.codec.ProtocolEncoder;
import com.tg.rpc.core.entity.ConfigConstant;
import com.tg.rpc.core.entity.QueueHolder;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.pool.ChannelPoolWrapper;
import com.tg.rpc.core.entity.Request;
import com.tg.rpc.core.handler.channel.ClientChannelHandler;
import com.tg.rpc.core.servicecenter.*;
import com.tg.rpc.core.servicecenter.Comparable;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by twogoods on 17/2/16.
 */
public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private String host;
    private int port;
    private int maxCapacity;
    private int requestTimeoutMillis;

    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private int borrowMaxWaitMillis;

    private String serverName;
    private ServiceDiscovery serviceDiscovery;

    private CopyOnWriteArrayList<ChannelPoolWrapper> channelPoolWrappers = new CopyOnWriteArrayList();

    EventLoopGroup group;
    Bootstrap b;

    private AtomicLong atomicLong = new AtomicLong(100000);

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public Client(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public static class Builder {
        private String host = ConfigConstant.DEFAULT_HOST;
        private int port = ConfigConstant.DEFAULT_PORT;
        private ServiceDiscovery serviceDiscovery;
        private int maxCapacity = ConfigConstant.DEFAULT_MAXCAPACITY;
        private int requestTimeoutMillis = ConfigConstant.DEFAULT_REQUESTIMEOUTMILLIS;
        private String serverName = ConfigConstant.DEFAULT_SERVICE_NAME;

        private int maxTotal = ConfigConstant.DEFAULT_POOL_MAXTOTAL;
        private int maxIdle = ConfigConstant.DEFAULT_POOL_MAXIDLE;
        private int minIdle = ConfigConstant.DEFAULT_POOL_MINIDLE;
        private int borrowMaxWaitMillis = ConfigConstant.DEFAULT_POOL_BORROWMAXWAITMILLIS;

        public Client.Builder host(String host) {
            this.host = host;
            return this;
        }

        public Client.Builder port(int port) {
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

        public Client.Builder serviceDiscovery(ServiceDiscovery serviceDiscovery) {
            this.serviceDiscovery = serviceDiscovery;
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
            client.serviceDiscovery = serviceDiscovery;
            client.preInit();
            client.init();
            return client;
        }
    }

    public void preInit() {
        //TODO NioEventLoopGroup里线程复用后设置合适的线程数.默认是cpu数的2倍,根据maxTotal的值适当选取
        group = new NioEventLoopGroup();
        b = new Bootstrap();
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
    }

    public Channel initConnection(String host, int port) {
        try {
            log.info("client try to connection {}:{}", host, port);
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
        if (serviceDiscovery == null) {
            addChannel(host, port);
            return;
        }

        log.info("Registry mode! service discover...");
        List<Service> serviceList = null;
        try {
            serviceList = serviceDiscovery.discover(serverName);
        } catch (Exception e) {
            log.error("discovery service error:{}", e);
        }
        for (Service service : serviceList) {
            addChannel(service);
        }
        final Comparable<ChannelPoolWrapper, Service> comparable = new Comparable<ChannelPoolWrapper, Service>() {
            @Override
            public boolean equals(ChannelPoolWrapper channelPoolWrapper, Service service) {
                return channelPoolWrapper.getHost().equals(service.getAddress()) && channelPoolWrapper.getPort() == service.getPort();
            }
        };

        try {
            serviceDiscovery.addListener(serverName, new ServiceChangeHandler() {
                @Override
                public void handle(List<Service> services) {
                    log.debug("execute listener :cache:{}, nowservices:{}", channelPoolWrappers, services);
                    List<ChannelPoolWrapper> shouldRemoved = ServiceFilter.filterRemoved(channelPoolWrappers, services, comparable);
                    List<Service> shouldAdded = ServiceFilter.filterAdded(channelPoolWrappers, services, comparable);
                    log.debug("listener: shouldRemoved:{}, shouldAdded:{}", shouldRemoved, shouldAdded);
                    for (ChannelPoolWrapper channelPoolWrapper : shouldRemoved) {
                        removeChannel(channelPoolWrapper);
                    }
                    for (Service service : shouldAdded) {
                        addChannel(service);
                    }
                }
            });
        } catch (Exception e) {
            log.error("serviceDiscovery set listener error:{}", e);
        }
    }

    private void addChannel(Service service) {
        channelPoolWrappers.add(new ChannelPoolWrapper(this, service.getAddress(), service.getPort()));
    }

    private void addChannel(String host, int port) {
        channelPoolWrappers.add(new ChannelPoolWrapper(this, host, port));
    }

    private void removeChannel(ChannelPoolWrapper channelPoolWrapper) {
        channelPoolWrapper.close();
        channelPoolWrappers.remove(channelPoolWrapper);
    }


    private ChannelPoolWrapper selectChannel() {
        //TODO 支持多种方式
        Random random = new Random();
        int size = channelPoolWrappers.size();
        if (size < 1) {
            return null;
        }
        int i = random.nextInt(size);
        return channelPoolWrappers.get(i);
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
