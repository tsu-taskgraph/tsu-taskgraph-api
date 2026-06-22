package ru.tsu_taskgraph.core_api.config;

import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.tsu_taskgraph.core_api.client.AiBridgeErrorDecoder;

@Configuration
public class FeignClientConfig {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new AiBridgeErrorDecoder();
    }
}