# HelloRPC
算是自己**从零编写一个RPC**的一个实践。实践下来，RPC主要有这么几个部分:通信包括连接建立消息传输，编解码要传输的内容，序列化和反序列化，最后是客户端和服务端的实现，客户调用一个服务的代理实现，发起对服务端的请求。使用现有的一些类库可以快速的完成一个RPC框架，如`netty`完成tcp传输,`protostuff`、`kryo`提供高效的序列化,`commons-pool2`也让我们实现连接池变得非常简单，至于服务的代理我们可以用jdk自身的`Proxy`或`Cglib`来完成。
# 现有功能
- 基本的客户端、服务端请求调用
- 客户端连接池，多个服务的接入
- SpringBoot集成
- 基于Consul和Zookeeper的服务注册发现
- 断路器

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

Server server = new Server.Builder()
             .port(9001)
             .serviceName("test")//服务名，全局唯一
             .serviceId("dev")//服务id，区分不同环境，建议 dev,test,prod
             .maxCapacity(3)
             .build();
//发布服务
server.addService(EchoService.class, new EchoServiceImpl())
       .addService(TestService.class, new TestServiceImpl());
server.start();
```
Client：

```
ClientProperty client = new ClientProperty();
client.serviceName("test")
        .provider("127.0.0.1:9001")
        .interfaces("com.tg.rpc.example.service.EchoService");
Client client = new Client.Builder().maxCapacity(3)
        .requestTimeoutMillis(3500)
        .connectionMaxTotal(10)
        .connectionMaxIdle(6)
        .client(client)
        .build();
DefaultClientInterceptor interceptor = new DefaultClientInterceptor(client);
ClientProxy clientProxy = new JdkClientProxy(interceptor);
EchoService echoService = clientProxy.getProxy(EchoService.class);
System.out.println(echoService.echo("twogoods"));
```
客户端可以接入多组服务

```
ClientProperty clientA = new ClientProperty();
clientA.serviceName("A")
        .provider("127.0.0.1:9001")
        .interfaces("com.tg.rpc.xxx.EchoService");
       
ClientProperty clientB = new ClientProperty();
clientB.serviceName("B")
        .provider("127.0.0.1:8090").provider("127.0.0.1:8080")//同一服务的多个实例
        .interfaces("com.tg.rpc.xxx.TestAService")//一个服务下的多个接口
        .interfaces("com.tg.rpc.xxx.TestBService");
        
Client client = new Client.Builder().maxCapacity(3)
        .requestTimeoutMillis(3500)
        .connectionMaxTotal(10)
        .connectionMaxIdle(6)
        .enableBreaker()
        .client(clientA)//加入两组服务
        .client(clientB)
        .build();
```
---

### 服务注册与发现
只需在server和client里增加相应的组件即可

```
//server端使用服务注册组件
ServiceRegistry serviceRegistry = ConsulCompentFactory.getRegistry("localhost", 8500);
Server server = new Server.Builder()
        .port(9001)
        .serviceName("testService")
        .serviceId("dev")
        .maxCapacity(3)
        .serviceRegistry(serviceRegistry)
        .build();
        
//client端使用服务发现组件     
ServiceDiscovery serviceDiscovery = ConsulCompentFactory.getDiscovery("localhost", 8500);
Client client = new Client.Builder()
        .serviceDiscovery(serviceDiscovery)
        .connectionMinIdle(1)
        .maxCapacity(3)
        .client(clientA)
        .build();
```
以上是基于consul的配置，使用zookeeper只需更换使用不同的组件即可

 ```
ServiceDiscovery serviceDiscovery = ZookeeperCompentFactory.getDiscovery("localhost",2181);
ServiceRegistry serviceRegistry = ZookeeperCompentFactory.getRegistry("localhost", 2181);
 ```
---
### 整合SpringBoot
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
    server:
        port: 9001
        serviceName: testService
        serviceId: dev
        registery: consul
        consulHost: 127.0.0.1
        consulPort: 8500
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
`application.yml`配置

```
tgrpc:
    client:
        registery: consul
        consulHost: 127.0.0.1
        consulPort: 8500
        maxCapacity: 3
        maxTotal: 3
        maxIdle: 3
        minIdle: 0
        borrowMaxWaitMillis: 5000
        clients:
            - serviceName: testService
              requestTimeoutMillis: 2000
              interfaces:
                    - com.tg.rpc.example.service.EchoService
                    - com.tg.rpc.example.service.TestService
              providerList: 127.0.0.1:8080
```
Client发起调用

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

### 断路器
熔断发生在客户端，默认的熔断策略：30秒内请求数大于60并且错误率超过50%触发熔断，熔断后每20秒过一个请求测试后端服务是否正常，调用成功则关闭熔断。
熔断期间的客户端不发送实际请求到服务端，如果你的服务接口使用了Java8接口里的默认方法，那么执行此默认方法，否则抛出`RequestRejectedException`异常，因此建议在定义接口的时候使用默认方法：

```
public interface TestServiceIface {
    default String test(String str) {
        return str;
    }
}
```
熔断组件最基本的使用如下：

```
BreakerProperty breakerProperty = new BreakerProperty().addClass("com.tg.rpc.breaker.TestServiceIface");//要监控的类
Breaker breaker = new Breaker(breakerProperty);
Method metricsMethod = TestServiceIface.class.getMethod("test", String.class, int.class);//熔断在方法级别
CommonTask task = new CommonTask(metricsMethod, new Object[]{"twogoods"}, new TestServiceIfaceImpl());
Object res=breaker.execute(task);
```
RPC框架提供了对熔断的支持，默认是关闭熔断的，开启只需修改配置

```
Client client = new Client.Builder()
        .requestTimeoutMillis(3500)
        .enableBreaker()//开启断路器
        .client(client)
        .build();
```
或者在SpringBoot的配置文件里增加

```
tgrpc:
    client:
        breaker: true
```
---
更多使用请参考[example](https://github.com/twogoods/HelloRpc/tree/master/example)模块
### TODO
- http调用的支持、异步编程、超时与重试、监控、限流(server)、降级开关(server)
- netty优化


