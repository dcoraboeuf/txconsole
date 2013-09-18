package net.txconsole.core.model;

import lombok.Data;

@Data
public class TranslationDiffControl {

    private final int entryId;
    private final String bundle;
    private final String section;
    private final String key;
    private final String message;

}
