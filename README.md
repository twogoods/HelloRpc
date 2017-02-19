# HelloRPC
算是自己**从零编写一个RPC**的一个实践.实践下来,完成一个helloworld级别的其实并不难,有这么几个部分:通信包括连接建立消息传输,
编解码要传输的内容,序列化和反序列化,最后是客户端和服务端的实现,客户调用一个服务的代理实现,发起对服务端的请求.
使用现有的一些类库可以快速的完成一个RPC框架,如`netty5.0`完成tcp传输,`protostuff`、`kryo`提供高效的序列化,
`commons-pool2`也让我们实现连接池变得非常简单,至于服务的代理我们可以用jdk自身的Proxy或Cglib来完成.

### 简单调用示例
定义服务和服务实现

```
public interface EchoService {
    String hello(String s);
}

public class EchoServiceImpl implements EchoService {
    @Override
    public String hello(String s) {
        return s;
    }
}
```
Server:

```
 //配置:端口号,最大的传输容量(单位M),服务响应的Handle
 Server server = new Server.Builder().port(9001).maxCapacity(3).build();
 //发布服务
 server.addService(EchoService.class, new EchoServiceImpl());
 server.start();
```
Client：

```
 Client client = new Client.Builder().host("127.0.0.1").port(9001).maxCapacity(3).build();
 DefaultClientInterceptor interceptor = new DefaultClientInterceptor(client);
 ClientProxy clientProxy = new JdkClientProxy(interceptor);

 EchoService echoService = clientProxy.getProxy(EchoService.class);
 System.out.println(echoService.hello("twogoods"));
```
---
### 整合Spring
#### 服务端配置
使用`@RpcService`注解

```
public interface Service {
    String test();
}

@RpcService(name="serviceImpl")
public class ServiceImpl {

    public String test() {
        return "hahah";
    }
}
```
启动server:

```
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(value = {"com.tg.rpc.springsupport.bean.server","com.tg.rpc.springsupport.config"})
public class ServerApplication {

    @Bean
    public SpringBeanResponseHandler springBeanResponseHandler() {
        return new SpringBeanResponseHandler();
    }

    @Bean
    public Server server(@Qualifier("rpcConfig") RpcConfig rpcConfig) {
        Server server = new Server.Builder().port(rpcConfig.getPort())
                .maxCapacity(rpcConfig.getMaxCapacity())
                .responseHandler(springBeanResponseHandler())
                .build();
        server.start();
        return server;
    }


    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ServerApplication.class);
    }
}
```
#### client配置
调用方使用`@RpcReferer`标注

```
@Component
public class ServiceCall {
    @RpcReferer(name = "serviceImpl")
    private Service service;

    public String call(){
        return service.test();
    }
}
```
启动Client:

```
@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.tg.rpc.springsupport.bean.client","com.tg.rpc.springsupport.config"})
public class ClientApplication {

    @Bean("defaultClient")
    public Client client(@Qualifier("rpcConfig") RpcConfig rpcConfig) {
        return new Client.Builder().host(rpcConfig.getHost())
                .port(rpcConfig.getPort())
                .maxCapacity(rpcConfig.getMaxCapacity())
                .requestTimeoutMillis(rpcConfig.getRequestTimeoutMillis())
                .connectionMaxTotal(rpcConfig.getMaxTotal())
                .connectionMaxIdle(rpcConfig.getMaxIdle())
                .connectionMinIdle(rpcConfig.getMinIdle())
                .connectionBorrowMaxWaitMillis(rpcConfig.getBorrowMaxWaitMillis())
                .build();
    }

    @Bean("defaultRpcClientInterceptor")
    public MethodInterceptor rpcClientInterceptor(@Qualifier("defaultClient") Client client) {
        return new DefaultClientInterceptor(client);
    }

    @Bean("cglibClientProxy")
    public ClientProxy cglibClientProxy(@Qualifier("defaultRpcClientInterceptor") MethodInterceptor rpcClientInterceptor) {
        return new CglibClientProxy(rpcClientInterceptor);
    }

    @Bean
    public RpcClientBeanPostProcessor rpcClientBeanPostProcessor(@Qualifier("cglibClientProxy") ClientProxy cglibClientProxy) {
        return new RpcClientBeanPostProcessor(cglibClientProxy);
    }

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ClientApplication.class);
        ServiceCall serviceCall = (ServiceCall) applicationContext.getBean("serviceCall");
        System.out.println(serviceCall.call());
    }
}
```
### TODO
- 优化Spring-Support,简化配置
- 服务注册于发现(Zookeeper,Consul)
- netty优化
