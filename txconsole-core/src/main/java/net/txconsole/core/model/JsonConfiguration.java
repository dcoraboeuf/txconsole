package net.txconsole.core.model;

import lombok.Data;
import org.codehaus.jackson.JsonNode;

@Data
public class JsonConfiguration {

    private final String id;
    private final JsonNode node;

}
