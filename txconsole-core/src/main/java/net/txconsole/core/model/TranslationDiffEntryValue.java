package net.txconsole.core.model;

import com.google.common.base.Predicate;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Locale;
import java.util.Map;

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

}
