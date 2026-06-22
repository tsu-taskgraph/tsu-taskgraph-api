package ru.tsu_taskgraph.core_api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tsu_taskgraph.core_api.dto.ai.EnrichTaskCallback;
import ru.tsu_taskgraph.core_api.service.EnrichmentCallbackService;

@RestController
@RequestMapping("/api/v1/internal")
@RequiredArgsConstructor
public class InternalCallbackController {

    private final EnrichmentCallbackService callbackService;

    @PostMapping("/enrichment-callback")
    public void enrichmentCallback(@RequestBody EnrichTaskCallback callback,
                                   @RequestHeader("X-Internal-Secret") String secret) {
        callbackService.processCallback(callback, secret);
    }
}