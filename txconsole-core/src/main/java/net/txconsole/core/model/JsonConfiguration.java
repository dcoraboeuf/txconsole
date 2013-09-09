package net.txconsole.core.model;

import lombok.Data;
import org.codehaus.jackson.JsonNode;

@Data
public class JsonConfiguration {

    private final String id;
    private final JsonNode node;

    public static JsonConfiguration fromJson(JsonNode node, String field) {
        JsonNode config = node.get(field);
        return new JsonConfiguration(
                config.get("id").asText(),
                config.get("node")
        );
    }
}
