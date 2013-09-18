package net.txconsole.core.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TranslationDiffControl {

    private final int entryId;
    private final String bundle;
    private final String section;
    private final String key;
    private final List<String> messages = new ArrayList<>();

    public TranslationDiffControl add(String message) {
        messages.add(message);
        return this;
    }

}
