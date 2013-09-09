package net.txconsole.service.support;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;

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
}
