package net.txconsole.service.support;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public abstract class AbstractConfigurable<C> extends AbstractDescriptible implements Configurable<C> {

    private final Class<? super C> configClass;

    public AbstractConfigurable(String id, String nameKey, String descriptionKey, Class<? super C> configClass) {
        super(id, nameKey, descriptionKey);
        this.configClass = configClass;
    }

    @Override
    public Class<? super C> getConfigClass() {
        return configClass;
    }
}
