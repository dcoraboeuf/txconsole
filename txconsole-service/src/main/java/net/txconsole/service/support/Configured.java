package net.txconsole.service.support;

import lombok.Data;
import net.txconsole.core.model.JsonConfiguration;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.io.IOException;
import java.util.Locale;

@Data
public class Configured<C, T extends Configurable<C>> {

    private final C configuration;
    private final T configurable;

    public JsonConfiguration getJsonConfiguration() throws IOException {
        return new JsonConfiguration(
                configurable.getId(),
                configurable.writeConfiguration(configuration)
        );
    }

    public Configured<C, T> withConfiguration(C configuration) {
        return new Configured<>(
                configuration,
                configurable
        );
    }
}
