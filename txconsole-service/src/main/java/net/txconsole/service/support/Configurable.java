package net.txconsole.service.support;

/**
 * Describes an object which can be configured.
 */
public interface Configurable<C> extends Descriptible {

    /**
     * Returns the type of the configuration class
     */
    Class<? super C> getConfigClass();
}
