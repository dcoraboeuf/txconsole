package net.txconsole.test;

import net.txconsole.core.model.TranslationDiff;
import net.txconsole.core.model.TranslationDiffEntry;
import net.txconsole.core.model.TranslationDiffEntryValue;
import net.txconsole.core.model.TranslationDiffType;
import net.txconsole.core.support.MapBuilder;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
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
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, null, "Added both"),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, null, "Ajout des deux")
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "added.default-only",
                                TranslationDiffType.ADDED,
                                MapBuilder.singleton(Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, null, "Added default only"))
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "deleted.both",
                                TranslationDiffType.DELETED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, "Initial value", null),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, "Valeur initiale", null)
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "deleted.default-only",
                                TranslationDiffType.DELETED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, "Initial value", null),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, "Valeur initiale", "Valeur initiale")
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "updated.both",
                                TranslationDiffType.UPDATED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, "Initial value", "Updated both"),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, "Valeur initiale", "Les deux sont modifiés")
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "updated.default-only",
                                TranslationDiffType.UPDATED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, "Initial value", "Updated default only"),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, "Valeur initiale", "Valeur initiale")
                                )
                        )
                )
        );
    }

    public static String uid(String prefix) {
        return prefix + new SimpleDateFormat("mmssSSS").format(new Date());
    }

}
