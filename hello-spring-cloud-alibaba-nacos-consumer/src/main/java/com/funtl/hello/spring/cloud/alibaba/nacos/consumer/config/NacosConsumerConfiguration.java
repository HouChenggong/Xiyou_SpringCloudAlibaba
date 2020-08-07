package com.funtl.hello.spring.cloud.alibaba.nacos.consumer.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author xiyou
 * 创建一个名为 NacosConsumerConfiguration 的 Java 配置类，主要作用是为了注入 RestTemplate
 */
@Configuration
public class NacosConsumerConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
