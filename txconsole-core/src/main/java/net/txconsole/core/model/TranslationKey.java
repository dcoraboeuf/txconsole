package net.txconsole.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TranslationKey {

    private final String bundle;
    private final String section;
    private final String name;

    public static TranslationKey key(String key) {
        return new TranslationKey(null, null, key);
    }

    public TranslationKey withBundle(String value) {
        return new TranslationKey(value, section, name);
    }

    public TranslationKey withSection(String value) {
        return new TranslationKey(bundle, value, name);
    }
}
