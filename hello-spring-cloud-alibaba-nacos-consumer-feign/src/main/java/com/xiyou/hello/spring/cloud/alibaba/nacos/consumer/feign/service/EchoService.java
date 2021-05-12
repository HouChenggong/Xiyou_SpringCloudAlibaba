package com.xiyou.hello.spring.cloud.alibaba.nacos.consumer.feign.service;


import com.xiyou.hello.spring.cloud.alibaba.nacos.consumer.feign.fallback.EchoServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 通过 @FeignClient("服务名") 注解来指定调用哪个服务。代码如下：
 */
@FeignClient(value = "nacos-provider",fallback = EchoServiceFallback.class)
public interface EchoService {

    @GetMapping(value = "/echo/{message}")
    String echo(@PathVariable("message") String message);
}
