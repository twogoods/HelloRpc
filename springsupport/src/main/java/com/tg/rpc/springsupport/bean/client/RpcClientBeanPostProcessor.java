package com.tg.rpc.springsupport.bean.client;

import com.tg.rpc.core.proxy.ClientProxy;
import com.tg.rpc.springsupport.annotation.RpcReferer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * Description:
 *   为service接口对象提供代理实现
 * @author twogoods
 * @version 0.1
 * @since 2017-02-01
 */
@Component
public class RpcClientBeanPostProcessor implements BeanPostProcessor {

    private ClientProxy clientProxy;

    public RpcClientBeanPostProcessor() {
    }

    public RpcClientBeanPostProcessor(ClientProxy clientProxy) {
        this.clientProxy = clientProxy;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            try {
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
}
