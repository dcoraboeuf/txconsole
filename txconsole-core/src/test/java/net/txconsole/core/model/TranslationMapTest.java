package net.txconsole.core.model;

import net.sf.jstring.builder.BundleBuilder;
import net.sf.jstring.builder.BundleCollectionBuilder;
import net.sf.jstring.builder.BundleKeyBuilder;
import net.sf.jstring.builder.BundleSectionBuilder;
import net.sf.jstring.model.Bundle;
import net.txconsole.core.config.JSONConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
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
    public void applyDiff() throws IOException {
        // Initial map
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
                                                                        .create("added.default-only")
                                                                        .addValue(Locale.ENGLISH, "Added default only")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder
                                                                        .create("updated.default-only")
                                                                        .addValue(Locale.ENGLISH, "Updated default only")
                                                                        .addValue(Locale.FRENCH, "Valeur initiale")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder
                                                                        .create("deleted.default-only")
                                                                        .addValue(Locale.FRENCH, "Valeur initiale")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder
                                                                        .create("added.both")
                                                                        .addValue(Locale.ENGLISH, "Added both")
                                                                        .addValue(Locale.FRENCH, "Ajout des deux")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder
                                                                        .create("updated.both")
                                                                        .addValue(Locale.ENGLISH, "Updated both")
                                                                        .addValue(Locale.FRENCH, "Modification des deux")
                                                        )
                                                /**
                                                 .key(
                                                 BundleKeyBuilder
                                                 .create("deleted.both")
                                                 .addValue(Locale.ENGLISH, "Initial value")
                                                 .addValue(Locale.FRENCH, "Valeur initiale")
                                                 )
                                                 */
                                        )
                                        .build()
                        )
                        .build()
        );
        // Applying a diff
        TranslationDiffBuilder diff = TranslationDiffBuilder.create();
        diff.added("common", "default", "added.default-only").withDiff(Locale.ENGLISH, null, "Added default"); // Computed
        diff.added("common", "default", "added.default-only").withDiff(Locale.FRENCH, null, "Ajout du défaut"); // Edited
        diff.updated("common", "default", "updated.default-only").withDiff(Locale.ENGLISH, "Initial valeur", "Updated default only"); // Computed
        diff.updated("common", "default", "updated.default-only").withDiff(Locale.FRENCH, "Valeur initiale", "Modification du défaut"); // Edited
        diff.deleted("common", "default", "deleted.default-only").withDiff(Locale.ENGLISH, "Initial valeur", null); // Computed
        // diff.deleted("common", "default", "deleted.default-only").withDiff(Locale.FRENCH, "Valeur initiale", null); // No edition allowed
        diff.added("common", "default", "added.both").withDiff(Locale.ENGLISH, null, "Added both"); // Computed
        diff.added("common", "default", "added.both").withDiff(Locale.FRENCH, null, "Ajout des deux"); // Computed
        diff.updated("common", "default", "updated.both").withDiff(Locale.ENGLISH, null, "Updated both"); // Computed
        diff.updated("common", "default", "updated.both").withDiff(Locale.FRENCH, null, "Modification des deux"); // Computed
        diff.deleted("common", "default", "deleted.both").withDiff(Locale.ENGLISH, "Initial value", null); // Computed
        diff.deleted("common", "default", "deleted.both").withDiff(Locale.FRENCH, "Valeur initiale", null); // Computed
        map = map.applyDiff(diff.build());
        // Checks the resulting map
        assertEquals(new TranslationMap(
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
                                                                        .create("added.default-only")
                                                                        .addValue(Locale.ENGLISH, "Added default only")
                                                                        .addValue(Locale.ENGLISH, "Ajout du défaut")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder
                                                                        .create("updated.default-only")
                                                                        .addValue(Locale.ENGLISH, "Updated default only")
                                                                        .addValue(Locale.FRENCH, "Modification du défaut")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder
                                                                        .create("added.both")
                                                                        .addValue(Locale.ENGLISH, "Added both")
                                                                        .addValue(Locale.FRENCH, "Ajout des deux")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder
                                                                        .create("updated.both")
                                                                        .addValue(Locale.ENGLISH, "Updated both")
                                                                        .addValue(Locale.FRENCH, "Modification des deux")
                                                        )
                                        )
                                        .build()
                        )
                        .build()
        ), map);
    }

}
