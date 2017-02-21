package com.tg.rpc.springsupport.annotation;

import com.tg.rpc.springsupport.config.RpcServerAutoConfiguration;
import org.springframework.context.annotation.Import;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RpcServerAutoConfiguration.class)
@Documented
public @interface EnableRpcServer {
}
