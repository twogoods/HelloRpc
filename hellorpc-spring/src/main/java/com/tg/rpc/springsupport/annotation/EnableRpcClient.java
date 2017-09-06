package com.tg.rpc.springsupport.annotation;

import com.tg.rpc.springsupport.config.RpcClientAutoConfiguration;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RpcClientAutoConfiguration.class)
@Documented
public @interface EnableRpcClient {
}
