package net.txconsole.backend.dao.model;

import lombok.Data;
import net.txconsole.core.model.TranslationDiffType;

@Data
public class TRequestEntry {

    private final int id;
    private final int request;
    private final String bundle;
    private final String section;
    private final String name;
    private final TranslationDiffType type;

}
