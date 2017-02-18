package com.tg.rpc.springsupport.bean.client;

import com.tg.rpc.core.bootstrap.Client;
import com.tg.rpc.core.proxy.CglibClientProxy;
import com.tg.rpc.core.proxy.ClientProxy;
import com.tg.rpc.core.proxy.MethodInterceptor;
import com.tg.rpc.core.proxy.RpcClientInterceptor;
import com.tg.rpc.springsupport.annotation.RpcReferer;
import com.tg.rpc.springsupport.config.RpcConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-01
 */
@Component
public class RpcClientBeanPostProcessor implements BeanPostProcessor {

    private ClientProxy clientProxy;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            try {
                //服务引用注解,若有自动发起client连接
                RpcReferer rpcReferer = f.getAnnotation(RpcReferer.class);
                if (rpcReferer != null) {
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    f.set(bean, clientProxy.getProxy(f.getType(), rpcReferer.name()));
                }
            } catch (Exception e) {
                throw new BeanInitializationException(
                        String.format("Failed to init remote service reference at field %s in class %s",
                                f.getName(), bean.getClass().getName()), e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Bean("defaultClient")
    public Client client(@Qualifier("rpcConfig") RpcConfig rpcConfig) {
        return new Client.Builder().host(rpcConfig.getHost())
                .port(rpcConfig.getPort())
                .maxCapacity(rpcConfig.getMaxCapacity())
                .requestTimeoutMillis(rpcConfig.getRequestTimeoutMillis())
                .connectionMaxTotal(rpcConfig.getMaxTotal())
                .connectionMaxIdle(rpcConfig.getMaxIdle())
                .connectionMinIdle(rpcConfig.getMinIdle())
                .connectionBorrowMaxWaitMillis(rpcConfig.getBorrowMaxWaitMillis())
                .build();
    }
    @Bean("defaultRpcClientInterceptor")
    public MethodInterceptor rpcClientInterceptor(@Qualifier("defaultClient") Client client){
        return new RpcClientInterceptor();
    }

    @Bean("cglibClientProxy")
    public ClientProxy cglibClientProxy(@Qualifier("defaultRpcClientInterceptor")MethodInterceptor rpcClientInterceptor){
        return new CglibClientProxy(rpcClientInterceptor);
    }

}
