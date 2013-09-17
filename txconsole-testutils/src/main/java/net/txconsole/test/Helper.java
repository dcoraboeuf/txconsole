package net.txconsole.test;

import net.txconsole.core.model.TranslationDiff;
import net.txconsole.core.model.TranslationDiffEntry;
import net.txconsole.core.model.TranslationDiffEntryValue;
import net.txconsole.core.model.TranslationDiffType;
import net.txconsole.core.support.MapBuilder;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;

public final class Helper {

    private Helper() {
    }

    public static String getResourceAsString(Class<?> root, String path) throws IOException {
        InputStream in = root.getResourceAsStream(path);
        if (in == null) {
            throw new IOException("Cannot find resource at " + path);
        } else {
            try {
                return IOUtils.toString(in, "UTF-8");
            } finally {
                in.close();
            }
        }
    }

    public static TranslationDiff sampleDiff() {
        return new TranslationDiff(
                Arrays.asList(
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "added.both",
                                TranslationDiffType.ADDED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(Locale.ENGLISH, false, null, "Added both"),
                                        Locale.FRENCH, new TranslationDiffEntryValue(Locale.FRENCH, false, null, "Ajout des deux")
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "added.default-only",
                                TranslationDiffType.ADDED,
                                MapBuilder.singleton(Locale.ENGLISH, new TranslationDiffEntryValue(Locale.ENGLISH, false, null, "Added default only"))
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "deleted.both",
                                TranslationDiffType.DELETED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(Locale.ENGLISH, false, "Initial value", null),
                                        Locale.FRENCH, new TranslationDiffEntryValue(Locale.FRENCH, false, "Valeur initiale", null)
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "deleted.default-only",
                                TranslationDiffType.DELETED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(Locale.ENGLISH, false, "Initial value", null),
                                        Locale.FRENCH, new TranslationDiffEntryValue(Locale.FRENCH, false, "Valeur initiale", null)
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "updated.both",
                                TranslationDiffType.UPDATED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(Locale.ENGLISH, false, "Initial value", "Updated both"),
                                        Locale.FRENCH, new TranslationDiffEntryValue(Locale.FRENCH, false, "Valeur initiale", "Les deux sont modifiés")
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "updated.default-only",
                                TranslationDiffType.UPDATED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(Locale.ENGLISH, false, "Initial value", "Updated default only"),
                                        Locale.FRENCH, new TranslationDiffEntryValue(Locale.FRENCH, true, "Valeur initiale", "Valeur initiale")
                                )
                        )
                )
        );
    }

}
