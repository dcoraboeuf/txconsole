package net.txconsole.core.model;

import lombok.Data;

import java.util.List;

@Data
public class TranslationDiff {

    private final List<TranslationDiffEntry> entries;

}
