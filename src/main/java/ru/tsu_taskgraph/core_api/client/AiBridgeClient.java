package ru.tsu_taskgraph.core_api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.tsu_taskgraph.core_api.config.FeignClientConfig;
import ru.tsu_taskgraph.core_api.dto.ai.*;

import java.util.List;

@FeignClient(name = "ai-bridge", url = "${ai-bridge.url}", configuration = FeignClientConfig.class)
public interface AiBridgeClient {

    @PostMapping("/api/v1/skeleton")
    GenerateSkeletonResponse generateSkeleton(@RequestHeader("X-Internal-Secret") String secret,
                                              @RequestBody GenerateSkeletonRequest request);

    @PostMapping("/api/v1/smart-recovery")
    SmartRecoveryResponse smartRecovery(@RequestHeader("X-Internal-Secret") String secret,
                                        @RequestBody SmartRecoveryRequest request);

    @GetMapping("/api/v1/providers")
    List<String> getProviders(@RequestHeader("X-Internal-Secret") String secret);

    @PostMapping("/api/v1/providers/models")
    List<String> getModels(@RequestHeader("X-Internal-Secret") String secret,
                           @RequestBody ProviderCheckRequest request);

    @PostMapping("/api/v1/enrich-task")
    EnrichTaskJobResponse enrichTask(@RequestHeader("X-Internal-Secret") String secret,
                                     @RequestBody EnrichTaskRequest request);

    @PostMapping("/api/v1/mutate")
    AiMutateGraphResponse mutateGraph(@RequestHeader("X-Internal-Secret") String secret,
                                      @RequestBody AiMutateGraphRequest request);
}