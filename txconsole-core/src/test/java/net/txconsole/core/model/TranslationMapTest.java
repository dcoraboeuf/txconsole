package net.txconsole.core.model;

import net.sf.jstring.builder.BundleBuilder;
import net.sf.jstring.builder.BundleCollectionBuilder;
import net.sf.jstring.builder.BundleKeyBuilder;
import net.sf.jstring.builder.BundleSectionBuilder;
import net.sf.jstring.model.Bundle;
import net.txconsole.core.config.JSONConfig;
import net.txconsole.core.support.MapBuilder;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class TranslationMapTest {

    private final ObjectMapper objectMapper = new JSONConfig().jacksonObjectMapper();

    @Test
    public void toJson_nogroup() throws IOException {
        TranslationMap map = new TranslationMap(
                "123456789abcdef",
                BundleCollectionBuilder
                        .create()
                        .bundle(
                                BundleBuilder
                                        .create("common")
                                        .section(
                                                BundleSectionBuilder
                                                        .create(Bundle.DEFAULT_SECTION)
                                                        .key(
                                                                BundleKeyBuilder
                                                                        .create("one")
                                                                        .addValue(Locale.ENGLISH, "One")
                                                                        .addValue(Locale.FRENCH, "Un")
                                                                        .addValue(Locale.GERMAN, "Eins")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder
                                                                        .create("two")
                                                                        .addValue(Locale.ENGLISH, "Two")
                                                                        .addValue(Locale.FRENCH, "Deux")
                                                                        .addValue(Locale.GERMAN, "Zwei")
                                                        )
                                        )
                                        .build()
                        )
                        .build()
        );
        String json = objectMapper.writeValueAsString(map);
        assertEquals("{\"version\":\"123456789abcdef\",\"bundleCollection\":{\"bundles\":[{\"name\":\"common\",\"comments\":[],\"sections\":[{\"name\":\"default\",\"comments\":[],\"keys\":[{\"name\":\"one\",\"comments\":[],\"values\":{\"en\":{\"comments\":[],\"value\":\"One\"},\"fr\":{\"comments\":[],\"value\":\"Un\"},\"de\":{\"comments\":[],\"value\":\"Eins\"}}},{\"name\":\"two\",\"comments\":[],\"values\":{\"en\":{\"comments\":[],\"value\":\"Two\"},\"fr\":{\"comments\":[],\"value\":\"Deux\"},\"de\":{\"comments\":[],\"value\":\"Zwei\"}}}]}]}]},\"supportedLocales\":[\"de\",\"en\",\"fr\"]}", json);
    }

    @Test
    public void diffAsMap() throws IOException {
        // Creates a diff
        TranslationDiff diff = new TranslationDiff(
                Arrays.asList(
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "added.both",
                                TranslationDiffType.ADDED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, false, null, "Added both"),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, false, null, "Ajout des deux")
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "added.default-only",
                                TranslationDiffType.ADDED,
                                MapBuilder.singleton(Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, false, null, "Added default only"))
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "deleted.both",
                                TranslationDiffType.DELETED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, false, "Initial value", null),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, false, "Valeur initiale", null)
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "deleted.default-only",
                                TranslationDiffType.DELETED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, false, "Initial value", null),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, false, "Valeur initiale", null)
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "updated.both",
                                TranslationDiffType.UPDATED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, false, "Initial value", "Updated both"),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, false, "Valeur initiale", "Les deux sont modifiés")
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "updated.default-only",
                                TranslationDiffType.UPDATED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, false, "Initial value", "Updated default only"),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, true, "Valeur initiale", "Valeur initiale")
                                )
                        )
                )
        );
        // Diff as a map
        TranslationMap map = TranslationMap.asMap(diff);
        // Checks
        String json = objectMapper.writeValueAsString(map);
        assertEquals("{\"version\":null,\"bundleCollection\":{\"bundles\":[{\"name\":\"common\",\"comments\":[],\"sections\":[{\"name\":\"default\",\"comments\":[],\"keys\":[{\"name\":\"added.both\",\"comments\":[],\"values\":{\"en\":{\"comments\":[],\"value\":\"Added both\"},\"fr\":{\"comments\":[],\"value\":\"Ajout des deux\"}}},{\"name\":\"added.default-only\",\"comments\":[],\"values\":{\"en\":{\"comments\":[],\"value\":\"Added default only\"}}},{\"name\":\"updated.both\",\"comments\":[],\"values\":{\"en\":{\"comments\":[],\"value\":\"Updated both\"},\"fr\":{\"comments\":[],\"value\":\"Les deux sont modifiés\"}}},{\"name\":\"updated.default-only\",\"comments\":[],\"values\":{\"en\":{\"comments\":[],\"value\":\"Updated default only\"},\"fr\":{\"comments\":[],\"value\":\"Valeur initiale\"}}}]}]}]},\"supportedLocales\":[\"en\",\"fr\"]}", json);
    }

}
