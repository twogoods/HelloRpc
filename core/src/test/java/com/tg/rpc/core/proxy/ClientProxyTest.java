package com.tg.rpc.core.proxy;


import org.junit.Test;

/**
 * Created by twogoods on 17/2/17.
 */
public class ClientProxyTest {

    @Test
    public void testJdk() {
        ClientProxy clientProxy = new JdkClientProxy(new TestInterceptor());

        AService a = clientProxy.getProxy(AService.class);
        System.out.println(a.test("haha"));

        BService b=clientProxy.getProxy(BService.class);
        System.out.println(b.b("twogoods"));
    }


    @Test
    public void testCglib() {
        ClientProxy clientProxy = new CglibClientProxy(new TestInterceptor());

        AService a = clientProxy.getProxy(AService.class);
        System.out.println(a.test("haha"));

        BService b=clientProxy.getProxy(BService.class);
        System.out.println(b.b("twogoods"));
    }


}