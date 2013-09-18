package net.txconsole.core.model;

import lombok.Data;

@Data
public class RequestControlledEntry {

    private final TranslationDiffEntry diffEntry;
    private final TranslationDiffControl diffControl;

}
