package com.tg.rpc.core.bootstrap;

import com.tg.rpc.core.codec.ProtocolEncoder;
import com.tg.rpc.core.entity.ConfigConstant;
import com.tg.rpc.core.exception.ServiceRegisterException;
import com.tg.rpc.core.handler.channel.ServerChannelHandler;
import com.tg.rpc.core.codec.ProtocolDecoder;
import com.tg.rpc.core.entity.ServiceHolder;
import com.tg.rpc.core.exception.ValidateException;
import com.tg.rpc.core.handler.response.ResponseHandler;
import com.tg.rpc.core.handler.response.DefaultResponseHandler;
import com.tg.rpc.core.servicecenter.Service;
import com.tg.rpc.core.servicecenter.ServiceRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by twogoods on 17/2/16.
 */
public class Server {
    private static final Logger log = LoggerFactory.getLogger(Server.class);

    private int port;
    private int maxCapacity;
    private String serviceName;
    private String serviceId;
    private ResponseHandler responseHandler;
    private ServiceRegistry serviceRegistry;


    private Server(int port, int maxCapacity, ResponseHandler responseHandler) {
        this.port = port;
        this.maxCapacity = maxCapacity;
        this.responseHandler = responseHandler;
    }

    private Server(int port, int maxCapacity, String serviceName, String serviceId, ResponseHandler responseHandler, ServiceRegistry serviceRegistry) {
        this.port = port;
        this.maxCapacity = maxCapacity;
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.responseHandler = responseHandler;
        this.serviceRegistry = serviceRegistry;
    }

    public static class Builder {
        private int port = ConfigConstant.DEFAULT_PORT;
        private int maxCapacity = ConfigConstant.DEFAULT_MAXCAPACITY;
        private ResponseHandler responseHandler = new DefaultResponseHandler();
        private ServiceRegistry serviceRegistry;
        private String serviceName;
        private String serviceId;

        public Server.Builder port(int port) {
            this.port = port;
            return this;
        }

        public Server.Builder maxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Server.Builder serviceName(String serviceName) {
            this.serviceName = serviceName;
            return this;
        }

        public Server.Builder serviceId(String serviceId) {
            this.serviceId = serviceId;
            return this;
        }

        public Server.Builder responseHandler(ResponseHandler responseHandler) {
            this.responseHandler = responseHandler;
            return this;
        }

        public Server.Builder serviceRegistry(ServiceRegistry serviceRegistry) {
            this.serviceRegistry = serviceRegistry;
            return this;
        }

        public Server build() {
            Validate.isTrue(port > 0, "port can't be negative, port:%d", port);
            Validate.isTrue(maxCapacity > 0, "maxCapacity can't be negative, maxCapacity:%d", maxCapacity);
            Validate.notEmpty(serviceName, "serviceName can't be empty");
            Validate.notEmpty(serviceId, "serverName can't be empty");
            Validate.notNull(responseHandler, "responseHandler can't be null");
            return new Server(port, maxCapacity, serviceName, serviceId, responseHandler, serviceRegistry);
        }
    }

    private EventLoopGroup bossGroup = new NioEventLoopGroup();
    private EventLoopGroup workerGroup = new NioEventLoopGroup();

    public void start() {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(ProtocolDecoder.serverDecoder(maxCapacity * 1024 * 1024))
                                    .addLast(new ProtocolEncoder())
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ServerChannelHandler(responseHandler));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("Server start success, in port:{}", port);
            if (serviceRegistry != null) {
                registerService();
            }
            channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    log.info("channel closed  {}", channelFuture.channel());
                }
            });
        } catch (Exception e) {
            log.error("server start failed!", e);
            System.exit(-1);
        }
    }

    private void registerService() throws Exception {
        Service service = new Service();
        service.setId(serviceId);
        service.setName(serviceName);
        service.setAddress(getLocalIp());
        service.setPort(port);
        service.setTtl(serviceRegistry.getTTL());
        try {
            serviceRegistry.register(service);
            log.info("Server register success.");
        } catch (Exception e) {
            throw new ServiceRegisterException(String.format("register service %s error!", service.toString()), e);
        }
    }

    private String getLocalIp() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.error("can't get local ip:{}", e);
        }
        return null;
    }

    public void shutdown() {
        if (serviceRegistry != null) {
            serviceRegistry.close();
        }
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public Server addService(Class serviceInterface, Object serviceImpl) throws ValidateException {
        ServiceHolder.addService(serviceInterface, serviceImpl);
        return this;
    }

    public Server addService(Class implClazz) throws Exception {
        ServiceHolder.addService(implClazz);
        return this;
    }

}
