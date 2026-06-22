package ru.tsu_taskgraph.core_api.dto.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import ru.tsu_taskgraph.core_api.entity.AiProvider;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AiMutateGraphResponse {
    private MutationPatch patch;
    private String modelUsed;
    private AiProvider provider;
}