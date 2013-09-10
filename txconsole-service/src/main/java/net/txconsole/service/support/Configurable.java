package net.txconsole.service.support;

import org.codehaus.jackson.JsonNode;

import java.io.IOException;

/**
 * Describes an object which can be configured.
 */
public interface Configurable<C> extends Descriptible {

    /**
     * Returns the type of the configuration class
     */
    Class<? super C> getConfigClass();

    C readConfiguration(JsonNode node) throws IOException;

    C readConfiguration(String json) throws IOException;

    JsonNode writeConfiguration(C config) throws IOException;

    String writeConfigurationAsJsonString(C config) throws IOException;
}
