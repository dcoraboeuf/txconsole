package net.txconsole.core.model;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@Data
public class TranslationDiff {

    private final List<TranslationDiffEntry> entries;

    public TranslationDiff forEdition(final List<Locale> locales) {
        return new TranslationDiff(
                new ArrayList<>(
                        Collections2.filter(
                                Lists.transform(
                                        entries,
                                        new Function<TranslationDiffEntry, TranslationDiffEntry>() {
                                            @Override
                                            public TranslationDiffEntry apply(TranslationDiffEntry entry) {
                                                return entry.forEdition(locales);
                                            }
                                        }
                                ),
                                Predicates.notNull()
                        )
                )
        );
    }

    public TranslationDiff sorted() {
        List<TranslationDiffEntry> newEntries = new ArrayList<>(entries);
        Collections.sort(newEntries);
        return new TranslationDiff(
                newEntries
        );
    }
}
