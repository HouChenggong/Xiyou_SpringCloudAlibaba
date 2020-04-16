package com.funtl.hello.spring.cloud.alibaba.nacos.consumer.feign.fallback;


import com.funtl.hello.spring.cloud.alibaba.nacos.consumer.feign.service.EchoService;
import org.springframework.stereotype.Component;

/**
 * 创建熔断器类并实现对应的 Feign 接口
 * @author xiyou
 */
@Component
public class EchoServiceFallback implements EchoService {
    @Override
    public String echo(String message) {
        return "echo fallback";
    }
}
