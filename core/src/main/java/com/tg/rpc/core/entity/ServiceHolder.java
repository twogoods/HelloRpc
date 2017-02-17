package com.tg.rpc.core.entity;

import com.tg.rpc.core.exception.ValidateException;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by twogoods on 17/2/17.
 */
public class ServiceHolder {

    private static Logger log = LogManager.getLogger(ServiceHolder.class);

    private static Map<String, Object> serviceMap = new HashMap<>();

    public static void addService(Class serviceInterface, Object serviceImpl) throws ValidateException {
        Class[] interfaces = serviceImpl.getClass().getInterfaces();
        boolean flag = false;
        for (Class clazz : interfaces) {
            if (clazz.getName().equals(serviceInterface.getName())) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new ValidateException(String.format("Object(%s) did't implements %s",
                    serviceImpl.getClass(), serviceInterface.getName()));
        }
        serviceMap.put(WordUtils.uncapitalize(serviceInterface.getSimpleName()), serviceImpl);
    }

    public static void addService(Class implClazz) throws Exception {
        Class[] interfaces = implClazz.getInterfaces();
        if (interfaces.length > 1) {
            throw new ValidateException("serviceImpl implements many interfaces, so change to use method: addService(Class serviceInterface, Object serviceImpl)");
        }
        try {
            serviceMap.put(WordUtils.uncapitalize(interfaces[0].getSimpleName()), implClazz.newInstance());
        } catch (InstantiationException e) {
            log.error("InstantiationException! change to use method: addService(Class serviceInterface, Object serviceImpl)");
            throw e;
        } catch (IllegalAccessException e) {
            log.error("InstantiationException! change to use method: addService(Class serviceInterface, Object serviceImpl)");
            throw e;
        }
    }

    public static Object get(Request request) {
        if (request.getServiceName() != null) {
            return serviceMap.get(request.getServiceName());
        }
        return serviceMap.get(WordUtils.uncapitalize(request.getClazz().getSimpleName()));
    }

}
