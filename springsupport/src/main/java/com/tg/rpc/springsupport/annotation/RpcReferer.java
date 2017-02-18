package com.tg.rpc.springsupport.annotation;

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
@Target({ElementType.FIELD})
public @interface RpcReferer {
    String name() default "";
}
