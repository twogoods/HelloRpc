package com.tg.rpc.consul;

import com.tg.rpc.springsupport.annotation.RpcService;

import java.lang.annotation.Annotation;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-21
 */
@RpcService
public class AnnotTest {
    public static void main(String[] args) {
        Annotation[] ans = AnnotTest.class.getAnnotations();
        Annotation[] all = ans[0].annotationType().getAnnotations();
        for(Annotation an:all){
            System.out.println(an);
        }
    }
}
