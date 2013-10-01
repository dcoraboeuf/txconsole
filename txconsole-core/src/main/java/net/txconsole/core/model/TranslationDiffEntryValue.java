package net.txconsole.core.model;

import com.google.common.base.Function;
import lombok.Data;

import java.util.Locale;

@Data
public class TranslationDiffEntryValue {

    private final int entryValueId;
    private final Locale locale;
    private final String oldValue;
    private final String newValue;

    public TranslationDiffEntryValue espace(Function<String, String> escapeFn) {
        return new TranslationDiffEntryValue(
                entryValueId,
                locale,
                escapeFn.apply(oldValue),
                escapeFn.apply(newValue)
        );
    }
}
