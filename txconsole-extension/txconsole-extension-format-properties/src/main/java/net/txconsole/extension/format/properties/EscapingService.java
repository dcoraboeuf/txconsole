package net.txconsole.extension.format.properties;

public interface EscapingService {

    /**
     * Escapes a value before writing it down to the properties files
     *
     * @param value Original value
     * @return Value to write into the properties file
     */
    String escape(String value);

}
