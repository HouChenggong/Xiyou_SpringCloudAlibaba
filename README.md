## 基本框架

### 统一依赖、提供者、消费者（2种）



- hello-spring-cloud-alibaba-dependencies
  是全局依赖
- hello-spring-cloud-alibaba-nacos-provider
  提供者
- hello-spring-cloud-alibaba-nacos-consumer-feign
  feign消费者
- hello-spring-cloud-alibaba-nacos-consumer
  普通restTemplate消费者

## 测试负载均衡
启动多个提供者服务，然后把端口号打印出来，然后消费者访问同一个接口，即可测试

而且不管消费者是哪个服务，都会自动负载均衡

```java
@SpringBootApplication
@EnableDiscoveryClient
public class NacosProviderApplication {
    public static void main(String[] args) {
        SpringApplication.run(NacosProviderApplication.class, args);
    }


    @Value("${server.port}")
    private String port;

    @RestController
    public class EchoController {
        @GetMapping(value = "/echo/{message}")
        public String echo(@PathVariable String message) {
            return "Hello Nacos Discovery " + message + " i am from port " + port;
        }
    }
}
```

## sentinel 消费者熔断阻止雪崩

- 消费者添加sentinel依赖

```java
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-alibaba-sentinel</artifactId>
</dependency>
```

- 消费者实现熔断接口

```java
import com.funtl.hello.spring.cloud.alibaba.nacos.consumer.feign.service.EchoService;
import org.springframework.stereotype.Component;

@Component
public class EchoServiceFallback implements EchoService {
    @Override
    public String echo(String message) {
        return "echo fallback";
    }
}
```

- 在原有的消费者接口上添加熔断接口fallBack

```java

import com.funtl.hello.spring.cloud.alibaba.nacos.consumer.feign.service.fallback.EchoServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "nacos-provider", fallback = EchoServiceFallback.class)
public interface EchoService {

    @GetMapping(value = "/echo/{message}")
    String echo(@PathVariable("message") String message);
}
```

- 测试熔断，只需要把服务提供者停掉即可

然后发现再次请求，就会返回熔断EchoServiceFallback接口中的内容

### sentinel仪表盘

- 下载sentinel仪表盘

```java
https://github.com/alibaba/Sentinel/releases
```

去里面下载一个最新的包，注意是sentinel-dashboardXX.jar

- 指定端口运行jar ，默认账户密码是sentinel sentinel

```bash
java -Dserver.port=8080 -Dcsp.sentinel.dashboard.server=localhost:8080 -Dproject.name=sentinel-dashboard -jar sentinel-dashboardXXX.jar
```

- 访问

```java
http://localhost:8080/#/dashboard/home
```

- 使用

在原来Feign的yml里面添加关于sentinel dashboard的相关内容,然后重启feign消费者服务

```java
spring:
  cloud:
    sentinel:
      transport:
        port: 8720
        dashboard: localhost:8080
```

## springcloudGateway网关

网关里面的配置

```yaml
spring:
  application:
    # 应用名称
    name: spring-gateway
  cloud:
    # 使用 Naoos 作为服务注册发现
    nacos:
      discovery:
        server-addr: 127.0.0.1:8848
    # 使用 Sentinel 作为熔断器
    sentinel:
      transport:
        port: 8722
        dashboard: localhost:8080
    # 路由网关配置
    gateway:
      # 设置与服务注册发现组件结合，这样可以采用服务名的路由策略
      discovery:
        locator:
          enabled: true
      # 配置路由规则
      routes:
        # 采用自定义路由 ID（有固定用法，不同的 id 有不同的功能，详见：https://cloud.spring.io/spring-cloud-gateway/2.0.x/single/spring-cloud-gateway.html#gateway-route-filters）
        - id: NACOS-CONSUMER
          # 采用 LoadBalanceClient 方式请求，以 lb:// 开头，后面的是注册在 Nacos 上的服务名
          uri: lb://nacos-consumer
          # Predicate 翻译过来是“谓词”的意思，必须，主要作用是匹配用户的请求，有很多种用法
          predicates:
            # Method 方法谓词，这里是匹配 GET 和 POST 请求
            - Method=GET,POST
        - id: NACOS-CONSUMER-FEIGN
          uri: lb://nacos-consumer-feign
          predicates:
            - Method=GET,POST

server:
  port: 9000

# 目前无效
feign:
  sentinel:
    enabled: true

# 目前无效
management:
  endpoints:
    web:
      exposure:
        include: "*"

# 配置日志级别，方别调试
logging:
  level:
    org.springframework.cloud.gateway: debug
```

- 测试

依次运行 Nacos 服务、`NacosProviderApplication`、`NacosConsumerApplication`、`NacosConsumerFeignApplication`、`GatewayApplication`

打开浏览器访问：http://localhost:9000/nacos-consumer/echo/app/name 浏览器显示

```html
Hello Nacos Discovery nacos-consumer i am from port 8082
```

打开浏览器访问：http://localhost:9000/nacos-consumer-feign/echo/hi 浏览器显示

```html
Hello Nacos Discovery Hi Feign i am from port 8082
```

**注意：请求方式是 http://路由网关IP:路由网关Port/服务名/\****

至此说明 Spring Cloud Gateway 的路由功能配置成功