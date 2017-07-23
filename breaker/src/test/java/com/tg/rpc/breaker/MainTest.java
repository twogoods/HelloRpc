package com.tg.rpc.breaker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

/**
 * Created by twogoods on 2017/7/21.
 */
public class MainTest {
    public static void main(String[] args) throws Throwable {
        Method method = TestServiceIface.class.getMethod("test", String.class);
//        MethodHandle methodHandle = MethodHandles.lookup().findVirtual(TestServiceIface.class, "test", MethodType.methodType(String.class));
//        System.out.println(methodHandle);

        Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
        constructor.setAccessible(true);
        Class declaringClass = TestServiceIface.class;
        MethodHandle methodHandle = constructor.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE).unreflectSpecial(method, declaringClass)
                .bindTo(Proxy.newProxyInstance(declaringClass.getClassLoader(), new Class[]{declaringClass}, (_proxy, _method, _args) -> null));
        Object res = methodHandle.invokeWithArguments("twogoods");
        System.out.println(res);
    }
}