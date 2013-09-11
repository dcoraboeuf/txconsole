package net.txconsole.core.model;

import lombok.Data;

import java.util.List;
import java.util.Locale;

/**
 * Truncated version of a {@link TranslationMap}, to be used at client side.
 */
@Data
public class TranslationMapResponse {

    private final int total;
    private final List<Locale> locales;
    private final List<TranslationEntry> entries;

}
