package net.txconsole.service.support;

import lombok.Data;
import org.codehaus.jackson.JsonNode;

import java.io.IOException;

@Data
public class Configured<C, T extends Configurable<C>> {

    private final C configuration;
    private final T configurable;

    public String writeConfigurationAsJsonString() throws IOException {
        return configurable.writeConfigurationAsJsonString(configuration);
    }

    public JsonNode writeConfiguration() throws IOException {
        return configurable.writeConfiguration(configuration);
    }
}
