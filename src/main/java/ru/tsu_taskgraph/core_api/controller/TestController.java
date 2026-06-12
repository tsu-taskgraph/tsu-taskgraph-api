package ru.tsu_taskgraph.core_api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import ru.tsu_taskgraph.core_api.config.OpenApiConfig;

@RestController
public class TestController {

    @GetMapping("/test")
    @Operation(
            summary = "Тестовый эндпоинт",
            security = @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME_NAME)
    )
    @ResponseBody()
    public String getTest() {
        return "Hello world!";
    }
}
