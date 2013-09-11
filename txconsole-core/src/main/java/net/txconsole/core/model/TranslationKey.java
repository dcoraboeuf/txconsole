package net.txconsole.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

@Data
@AllArgsConstructor
public class TranslationKey implements Comparable<TranslationKey> {

    private final String category;
    private final String group;
    private final String key;

    public static TranslationKey key(String key) {
        return new TranslationKey(null, null, key);
    }

    public TranslationKey withGroup(String name) {
        return new TranslationKey(category, name, key);
    }

    @Override
    public int compareTo(TranslationKey o) {
        return ObjectUtils.compare(key, o.key);
    }
}
