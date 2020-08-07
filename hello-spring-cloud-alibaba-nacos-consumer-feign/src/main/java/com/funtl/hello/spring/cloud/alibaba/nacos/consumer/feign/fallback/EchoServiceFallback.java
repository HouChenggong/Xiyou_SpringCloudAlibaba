package com.funtl.hello.spring.cloud.alibaba.nacos.consumer.feign.fallback;


import com.funtl.hello.spring.cloud.alibaba.nacos.consumer.feign.service.EchoService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * 创建熔断器类并实现对应的 Feign 接口
 *
 * @author xiyou
 */
@Component
public class EchoServiceFallback implements EchoService {
    @Override
    public String echo(String message) {
        System.out.println("执行了fall back");
        return "echo fallback";
    }
}
