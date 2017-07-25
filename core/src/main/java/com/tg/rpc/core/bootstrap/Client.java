package com.tg.rpc.core.bootstrap;

import com.tg.rpc.breaker.Breaker;
import com.tg.rpc.breaker.BreakerProperty;
import com.tg.rpc.core.codec.ProtocolDecoder;
import com.tg.rpc.core.codec.ProtocolEncoder;
import com.tg.rpc.core.config.ClientProperty;
import com.tg.rpc.core.entity.ConfigConstant;
import com.tg.rpc.core.entity.QueueHolder;
import com.tg.rpc.core.entity.Response;
import com.tg.rpc.core.exception.ClientMissingException;
import com.tg.rpc.core.exception.ServiceInvokeTimeOutException;
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
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by twogoods on 17/2/16.
 */
@Data
public class Client {
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    private int maxCapacity;
    private int requestTimeoutMillis;
    private int maxTotal;
    private int maxIdle;
    private int minIdle;
    private int borrowMaxWaitMillis;
    private boolean breakerable;
    private List<ClientProperty> clients = new ArrayList<>();

    private ServiceDiscovery serviceDiscovery;

    private Map<String, CopyOnWriteArrayList<ChannelPoolWrapper>> clientPools = new HashMap<>();
    private Map<String, String> serviceCache = new HashMap<>();
    private Breaker breaker;

    private EventLoopGroup group;
    private Bootstrap bootstrap;

    //TODO 改造id生成器
    private AtomicLong requestId = new AtomicLong(100000);

    public Client() {
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

        private int maxTotal = ConfigConstant.DEFAULT_POOL_MAXTOTAL;
        private int maxIdle = ConfigConstant.DEFAULT_POOL_MAXIDLE;
        private int minIdle = ConfigConstant.DEFAULT_POOL_MINIDLE;
        private int borrowMaxWaitMillis = ConfigConstant.DEFAULT_POOL_BORROWMAXWAITMILLIS;
        private boolean breakerable = false;
        private List<ClientProperty> clients = new ArrayList<>();

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

        public Client.Builder client(ClientProperty clientProperty) {
            this.clients.add(clientProperty);
            return this;
        }

        public Client.Builder clients(List<ClientProperty> clients) {
            this.clients.addAll(clients);
            return this;
        }

        public Client.Builder enableBreaker() {
            this.breakerable = true;
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
                client = new Client();
            }
            client.maxCapacity = this.maxCapacity;
            client.requestTimeoutMillis = this.requestTimeoutMillis;
            client.maxTotal = this.maxTotal;
            client.maxIdle = this.maxIdle;
            client.minIdle = this.minIdle;
            client.borrowMaxWaitMillis = this.borrowMaxWaitMillis;
            client.serviceDiscovery = this.serviceDiscovery;
            client.clients = this.clients;
            client.breakerable = this.breakerable;
            client.preInit();
            client.init();
            return client;
        }
    }

    public void preInit() {
        //NioEventLoopGroup里线程复用后设置合适的线程数.默认是cpu数的2倍,即认为一个线程一半时间在做io
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, borrowMaxWaitMillis)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(ProtocolDecoder.clientDecoder(maxCapacity * 1024 * 1024))
                                .addLast(new ProtocolEncoder())
                                .addLast(new LoggingHandler(LogLevel.DEBUG))
                                .addLast(new ClientChannelHandler());
                    }
                });
    }

    public Channel initConnection(String host, int port) {
        try {
            log.info("client try to connection {}:{}", host, port);
            ChannelFuture f = bootstrap.connect(host, port).sync();
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
        initPoolMapAndParse();
        if (serviceDiscovery == null) {
            initChannelInDefaultMode();
            return;
        }
        initChannelInRegistryMode();
        log.info("Registry mode! {} discover services done.", serviceDiscovery.getClass().getSimpleName());
    }

    private void createBraker() {
        BreakerProperty breakerProperty = new BreakerProperty();
        clients.forEach(clientProperty -> {
            breakerProperty.addClass(clientProperty.getInterfaces());
        });
        breaker = new Breaker(breakerProperty);
    }

    private void parseClientProperty(ClientProperty clientProperty) {
        clientProperty.getInterfaces().forEach(iface -> serviceCache.put(iface, clientProperty.getServiceName()));
    }

    private void initPoolMapAndParse() {
        clients.forEach(clientProperty -> {
            clientPools.put(clientProperty.getServiceName(), new CopyOnWriteArrayList<>());
            parseClientProperty(clientProperty);
        });
        if (breakerable) {
            createBraker();
        }
    }

    private void initChannelInDefaultMode() {
        clients.forEach(clientProperty -> {
            addChannel(clientProperty);
        });
    }

    private void initChannelInRegistryMode() {
        for (ClientProperty clientProperty : clients) {
            List<Service> serviceList = null;
            try {
                serviceList = serviceDiscovery.discover(clientProperty.getServiceName());
            } catch (Exception e) {
                log.error("discovery service error:{}", e);
                continue;
            }
            if (serviceList.size() == 0) {
                log.warn("can't get any provider for service:{}", clientProperty.getServiceName());
                continue;
            }
            serviceList.forEach(service -> addChannel(service));
            final Comparable<ChannelPoolWrapper, Service> comparable = (ChannelPoolWrapper channelPoolWrapper, Service service)
                    -> channelPoolWrapper.getHost().equals(service.getAddress()) && channelPoolWrapper.getPort() == service.getPort();
            try {
                serviceDiscovery.addListener(clientProperty.getServiceName(), services -> {
                    List<ChannelPoolWrapper> channelPoolWrappers = clientPools.get(clientProperty.getServiceName());
                    log.debug("execute listener :cache:{}, nowservices:{}", clientPools.get(clientProperty.getServiceName()), services);
                    List<ChannelPoolWrapper> shouldRemoved = ServiceFilter.filterRemoved(channelPoolWrappers, services, comparable);
                    List<Service> shouldAdded = ServiceFilter.filterAdded(channelPoolWrappers, services, comparable);
                    log.debug("listener: shouldRemoved:{}, shouldAdded:{}", shouldRemoved, shouldAdded);
                    for (ChannelPoolWrapper channelPoolWrapper : shouldRemoved) {
                        removeChannel(clientProperty.getServiceName(), channelPoolWrapper);
                    }
                    for (Service service : shouldAdded) {
                        addChannel(service);
                    }
                });
            } catch (Exception e) {
                log.error("serviceDiscovery set listener error:{}", e);
            }
        }
    }

    private void addChannel(Service service) {
        clientPools.get(service.getName()).add(new ChannelPoolWrapper(this, service.getAddress(), service.getPort()));
    }

    private static final String HTTP_PROTOCOL = "http://";

    private void addChannel(ClientProperty clientProperty) {
        clientProperty.getProviderList().forEach(provider -> {
            try {
                URL providerUrl = new URL(HTTP_PROTOCOL + provider);
                clientPools.get(clientProperty.getServiceName()).add(new ChannelPoolWrapper(this, providerUrl.getHost(), providerUrl.getPort()));
            } catch (MalformedURLException e) {
                log.error("providerList parse error, origin content :{}", provider);
            }
        });
    }

    private void removeChannel(String serviceName, ChannelPoolWrapper channelPoolWrapper) {
        channelPoolWrapper.close();
        clientPools.get(serviceName).remove(channelPoolWrapper);
    }

    private ChannelPoolWrapper selectChannel(String serviceName) {
        //TODO 负载均衡算法、轮询、权重、hash、一致性哈希
        Random random = new Random();
        List<ChannelPoolWrapper> channelPoolWrappers = clientPools.get(serviceName);
        int size = channelPoolWrappers.size();
        if (size < 1) {
            return null;
        }
        int i = random.nextInt(size);
        return channelPoolWrappers.get(i);
    }

    public Response sendRequest(Request request) throws Exception {
        request.setRequestId(requestId.incrementAndGet());
        String serviceName = serviceCache.get(request.getClazz().getName());
        if (StringUtils.isEmpty(serviceName)) {
            throw new ClientMissingException(String.format("can't get service %s , config it in 'interfaces' properity", request.getClazz().getName()));
        }
        ChannelPoolWrapper channelPoolWrapper = selectChannel(serviceName);
        Validate.notNull(channelPoolWrapper, "channel pool did'n init");
        Channel channel = channelPoolWrapper.getObject();
        Validate.notNull(channel, "can't get channel from pool");
        BlockingQueue<Response> blockingQueue = new ArrayBlockingQueue(1);
        QueueHolder.put(request.getRequestId(), blockingQueue);
        channel.writeAndFlush(request);
        try {
            return blockingQueue.poll(requestTimeoutMillis << 1, TimeUnit.MILLISECONDS);
        } finally {
            channelPoolWrapper.returnObject(channel);
            QueueHolder.remove(request.getRequestId());
        }
    }
}
