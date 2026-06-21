package ru.tsu_taskgraph.core_api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.tsu_taskgraph.core_api.config.FeignClientConfig;
import ru.tsu_taskgraph.core_api.dto.ai.GenerateSkeletonRequest;
import ru.tsu_taskgraph.core_api.dto.ai.GenerateSkeletonResponse;
import ru.tsu_taskgraph.core_api.dto.ai.SmartRecoveryRequest;
import ru.tsu_taskgraph.core_api.dto.ai.SmartRecoveryResponse;

@FeignClient(name = "ai-bridge", url = "${ai-bridge.url}", configuration = FeignClientConfig.class)
public interface AiBridgeClient {

    @PostMapping("/api/v1/ai/skeleton")
    GenerateSkeletonResponse generateSkeleton(@RequestHeader("X-Internal-Secret") String secret,
                                              @RequestBody GenerateSkeletonRequest request);

    @PostMapping("/api/v1/ai/smart-recovery")
    SmartRecoveryResponse smartRecovery(@RequestHeader("X-Internal-Secret") String secret,
                                        @RequestBody SmartRecoveryRequest request);
}