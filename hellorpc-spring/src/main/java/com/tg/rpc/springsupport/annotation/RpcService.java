package com.tg.rpc.springsupport.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * Description:
 *
 * @author twogoods
 * @version 0.1
 * @since 2017-02-01
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Component()
public @interface RpcService {
    String name() default "";
}
