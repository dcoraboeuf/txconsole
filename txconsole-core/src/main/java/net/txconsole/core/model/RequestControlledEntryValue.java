package net.txconsole.core.model;

import lombok.Data;

import java.util.Locale;
import java.util.Map;

@Data
public class RequestControlledEntryValue {

    private final TranslationDiffEntryValue entryValue;
    private final Map<Locale, String> messages;

}
