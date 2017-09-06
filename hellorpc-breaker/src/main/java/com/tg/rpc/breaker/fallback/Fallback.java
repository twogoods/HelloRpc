package com.tg.rpc.breaker.fallback;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by twogoods on 2017/7/23.
 */
public class Fallback {
    private final static Constructor<MethodHandles.Lookup> constructor;

    static {
        try {
            constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
            constructor.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final boolean supportFallback;
    private final MethodHandle methodHandle;

    public Fallback(Method method) {
        try {
            supportFallback = method.isDefault();
            if (supportFallback) {
                final Class<?> declaringClass = method.getDeclaringClass();
                methodHandle = constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE).unreflectSpecial(method, declaringClass)
                        .bindTo(Proxy.newProxyInstance(declaringClass.getClassLoader(), new Class[]{declaringClass}, (_proxy, _method, _args) -> null));
            } else {
                methodHandle = null;
            }
        } catch (ReflectiveOperationException t) {
            throw new RuntimeException(t);
        }
    }

    public boolean supportFallback() {
        return supportFallback;
    }

    public Object callFallback(Object[] args) throws Throwable {
        return methodHandle.invokeWithArguments(args);
    }
}