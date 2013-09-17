package net.txconsole.core.model;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Locale;
import java.util.Map;

@Data
public class TranslationDiffEntryValue {

    private final Locale locale;
    private final boolean toUpdate;
    private final String oldValue;
    private final String newValue;

}
