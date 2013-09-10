package net.txconsole.service.support;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public abstract class AbstractSimpleConfigurable<C> extends AbstractConfigurable<C> {

    private final ObjectMapper objectMapper;

    public AbstractSimpleConfigurable(String id, String nameKey, String descriptionKey, Class<? super C> configClass, ObjectMapper objectMapper) {
        super(id, nameKey, descriptionKey, configClass);
        this.objectMapper = objectMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public C readConfiguration(JsonNode node) throws IOException {
        return (C) objectMapper.readValue(node, getConfigClass());
    }

    @Override
    public JsonNode writeConfiguration(C config) throws IOException {
        return objectMapper.valueToTree(config);
    }

    @Override
    public String writeConfigurationAsJsonString(C config) throws IOException {
        return objectMapper.writeValueAsString(config);
    }
}
