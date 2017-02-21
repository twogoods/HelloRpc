# HelloRPC
算是自己**从零编写一个RPC**的一个实践.实践下来,完成一个helloworld级别的其实并不难,有这么几个部分:通信包括连接建立消息传输,
编解码要传输的内容,序列化和反序列化,最后是客户端和服务端的实现,客户调用一个服务的代理实现,发起对服务端的请求.

使用现有的一些类库可以快速的完成一个RPC框架,如`netty5.0`完成tcp传输,`protostuff`、`kryo`提供高效的序列化,
`commons-pool2`也让我们实现连接池变得非常简单,至于服务的代理我们可以用jdk自身的`Proxy`或`Cglib`来完成.

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
 System.out.println(echoService.echo("twogoods"));
```
---
### 整合Spring
#### 服务端配置
使用`@RpcService`注解

```
public interface EchoService {
    String echo(String s);
}

@RpcService
public class EchoServiceImpl implements EchoService {
    @Override
    public String echo(String s) {
        return s;
    }
}
```
`application.yml`配置

```
tgrpc:
    host: 127.0.0.1
    port: 9100
    maxCapacity: 3
    requestTimeoutMillis: 8000
    maxTotal: 3
    maxIdle: 3
    minIdle: 0
    borrowMaxWaitMillis: 5000
```
启动server:

```
@SpringBootApplication
@EnableAutoConfiguration
@EnableRpcServer //启用Server
@ComponentScan()
public class ServerApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ServerApplication.class);
    }
}
```
#### client配置
调用方使用`@RpcReferer`注解.

```
@Component
public class ServiceCall {

    @RpcReferer
    private EchoService echoService;

    public String echo(String s) {
        return echoService.echo(s);
    }
}
```
启动Client发起调用

```
@SpringBootApplication
@EnableAutoConfiguration
@EnableRpcClient //启用Client
@ComponentScan(basePackages = {"com.tg.rpc.springsupport.bean.client"})
public class ClientApplication {

    public static void main(String[] args) {
        ApplicationContext applicationContext = SpringApplication.run(ClientApplication.class);
        ServiceCall serviceCall = (ServiceCall) applicationContext.getBean("serviceCall");
        System.out.println("echo return :" + serviceCall.echo("TgRPC"));
    }
}
```
### TODO
- 服务注册与发现(Zookeeper,Consul)
- netty优化
