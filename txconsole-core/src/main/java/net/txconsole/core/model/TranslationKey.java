package net.txconsole.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranslationKey {

    private final String category;
    private final String group;
    private final String key;

    public TranslationKey(String key) {
        this(null, null, key);
    }
}
