package ru.tsu_taskgraph.core_api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.tsu_taskgraph.core_api.dto.ai.GenerateSkeletonRequest;
import ru.tsu_taskgraph.core_api.dto.ai.GenerateSkeletonResponse;

@FeignClient(name = "ai-bridge", url = "${ai-bridge.url}")
public interface AiBridgeClient {

    @PostMapping("/api/v1/ai/skeleton")
    GenerateSkeletonResponse generateSkeleton(@RequestHeader("X-Internal-Secret") String secret,
                                              @RequestBody GenerateSkeletonRequest request);
}