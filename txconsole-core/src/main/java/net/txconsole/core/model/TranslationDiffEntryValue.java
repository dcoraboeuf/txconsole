package net.txconsole.core.model;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import lombok.Data;

import java.util.Locale;

@Data
public class TranslationDiffEntryValue {

    public static final Predicate<TranslationDiffEntryValue> entryValueEditableFn = new Predicate<TranslationDiffEntryValue>() {
        @Override
        public boolean apply(TranslationDiffEntryValue entryValue) {
            return entryValue.isEditable();
        }
    };
    private final int entryValueId;
    private final Locale locale;
    private final boolean editable;
    private final String oldValue;
    private final String newValue;

    public TranslationDiffEntryValue espace(Function<String, String> escapeFn) {
        return new TranslationDiffEntryValue(
                entryValueId,
                locale,
                editable,
                escapeFn.apply(oldValue),
                escapeFn.apply(newValue)
        );
    }
}
