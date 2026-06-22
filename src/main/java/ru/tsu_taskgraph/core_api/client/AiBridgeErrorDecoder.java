package ru.tsu_taskgraph.core_api.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.tsu_taskgraph.core_api.dto.ai.AiErrorResponse;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
public class AiBridgeErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Exception decode(String methodKey, Response response) {
        AiErrorResponse errorResponse = null;
        try (InputStream bodyIs = response.body().asInputStream()) {
            errorResponse = objectMapper.readValue(bodyIs, AiErrorResponse.class);
        } catch (IOException e) {
            log.error("Не удалось прочитать тело ошибки от AiBridge", e);
        }

        String message = errorResponse != null ? errorResponse.getMessage() : "Неизвестная ошибка от AiBridge";

        return switch (response.status()) {
            case 400, 401, 429, 502, 504 -> new AiProviderException(message, errorResponse);
            case 422 -> new AiValidationException(message, errorResponse);
            default -> new Exception(message);
        };
    }

    @Getter
    public static class AiProviderException extends RuntimeException {
        private final AiErrorResponse errorResponse;

        public AiProviderException(String message, AiErrorResponse errorResponse) {
            super(message);
            this.errorResponse = errorResponse;
        }
    }

    @Getter
    public static class AiValidationException extends RuntimeException {
        private final AiErrorResponse errorResponse;

        public AiValidationException(String message, AiErrorResponse errorResponse) {
            super(message);
            this.errorResponse = errorResponse;
        }
    }
}