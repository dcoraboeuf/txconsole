package net.txconsole.service.support;

import net.txconsole.service.EscapingService;

/**
 * No escaping at all.
 */
public class NOPEscapingService implements EscapingService {

    /**
     * No escaping
     */
    @Override
    public String write(String value) {
        return value;
    }

    /**
     * No escaping.
     */
    @Override
    public String read(String value) {
        return value;
    }

}
