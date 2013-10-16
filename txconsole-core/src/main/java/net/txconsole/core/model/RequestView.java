package net.txconsole.core.model;

import lombok.Data;

import java.util.Locale;
import java.util.Set;

@Data
public class RequestView {

    private final RequestSummary summary;
    private final Set<Locale> locales;
    private final TranslationDiff diff;

}
