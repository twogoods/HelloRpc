package com.tg.rpc.core.bootstrap;

import com.tg.rpc.core.codec.ProtocolEncoder;
import com.tg.rpc.core.handler.channel.ServerChannelHandler;
import com.tg.rpc.core.codec.ProtocolDecoder;
import com.tg.rpc.core.entity.ServiceHolder;
import com.tg.rpc.core.exception.ValidateException;
import com.tg.rpc.core.handler.response.ResponseHandler;
import com.tg.rpc.core.handler.response.DefaultResponseHandler;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by twogoods on 17/2/16.
 */
public class Server {
    private static Logger log = LogManager.getLogger(Server.class);

    private int port;
    private int maxCapacity;
    private String serverName;
    private ResponseHandler responseHandler;


    private Server(int port, int maxCapacity, ResponseHandler responseHandler) {
        this.port = port;
        this.maxCapacity = maxCapacity;
        this.responseHandler = responseHandler;
    }

    public static class Builder {
        private int port;
        private int maxCapacity;
        private ResponseHandler responseHandler = new DefaultResponseHandler();

        public Server.Builder port(int port) {
            this.port = port;
            return this;
        }

        public Server.Builder maxCapacity(int maxCapacity) {
            this.maxCapacity = maxCapacity;
            return this;
        }

        public Server.Builder responseHandler(ResponseHandler responseHandler) {
            this.responseHandler = responseHandler;
            return this;
        }

        public Server build() {
            Validate.isTrue(port > 0, "port can't be negative, port:%d", port);
            Validate.isTrue(maxCapacity > 0, "maxCapacity can't be negative, maxCapacity:%d", maxCapacity);
            Validate.notNull(responseHandler, "responseHandler can't be null");
            return new Server(port, maxCapacity, responseHandler);
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
                            ch.pipeline().addLast(new ProtocolDecoder(maxCapacity * 1024 * 1024))
                                    .addLast(new ProtocolEncoder())
                                    .addLast(new LoggingHandler(LogLevel.DEBUG))
                                    .addLast(new ServerChannelHandler(responseHandler));
                        }
                    });
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("Server start success, in port:{}", port);
            channelFuture.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    log.info("channel closed  {}", channelFuture.channel());
                }
            });
        } catch (Exception e) {
            log.error("server start failed!", e);
        }
    }

    public void shutdown() {
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
