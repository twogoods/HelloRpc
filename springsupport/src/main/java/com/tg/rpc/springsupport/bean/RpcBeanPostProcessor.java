package com.tg.rpc.springsupport.bean;

import com.tg.rpc.springsupport.annotation.RpcReferer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.lang.reflect.Field;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-01
 */
public class RpcBeanPostProcessor implements BeanPostProcessor, BeanFactoryPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {


        Class<?> clazz = bean.getClass();

        Field[] fields = clazz.getDeclaredFields();
        for (Field f : fields) {
            try {
                RpcReferer rpcReferer = f.getAnnotation(RpcReferer.class);
                if (rpcReferer != null) {
                    if (!f.isAccessible()) {
                        f.setAccessible(true);
                    }
                    f.set(bean, new Object());
                }
            } catch (Exception e) {
                throw new BeanInitializationException("Failed to init remote service reference at field " + f.getName()
                        + " in class " + bean.getClass().getName(), e);
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
    }
}
