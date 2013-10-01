package net.txconsole.service;

public interface EscapingService {

    /**
     * Escapes a value before writing it down to the properties files
     *
     * @param value Original value
     * @return Value to write into the properties file
     */
    String escapeForStorage(String value);

    /**
     * Escapes a value before editing it
     *
     * @param value Stored value
     * @return Value to show to the editor
     */
    String escapeForEdition(String value);

}
