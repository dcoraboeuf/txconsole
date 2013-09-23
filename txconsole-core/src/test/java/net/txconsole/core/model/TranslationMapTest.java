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
    public void merge() throws IOException {
        // Old map
        TranslationMap oldMap = new TranslationMap(
                "1",
                BundleCollectionBuilder.create()
                        .bundle(
                                BundleBuilder.create("common")
                                        .section(
                                                BundleSectionBuilder
                                                        .create(Bundle.DEFAULT_SECTION)
                                                        .key(
                                                                BundleKeyBuilder.create("added.default-only")
                                                                        .addValue(Locale.ENGLISH, "Added default only")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("added.both")
                                                                        .addValue(Locale.ENGLISH, "Added both")
                                                                        .addValue(Locale.FRENCH, "Ajout des deux")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("updated.default-only")
                                                                        .addValue(Locale.ENGLISH, "Updated default only")
                                                                        .addValue(Locale.FRENCH, "Valeur initiale")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("updated.both")
                                                                        .addValue(Locale.ENGLISH, "Updated both")
                                                                        .addValue(Locale.FRENCH, "Les deux sont modifiés")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("deleted.default-only")
                                                                        .addValue(Locale.FRENCH, "Valeur initiale")
                                                        )
                                                                // .key(
                                                                //        BundleKeyBuilder.create("deleted.both")
                                                                // )
                                                        .key(
                                                                BundleKeyBuilder.create("unchanged")
                                                                        .addValue(Locale.ENGLISH, "Unchanged")
                                                                        .addValue(Locale.FRENCH, "Inchangé")
                                                        )
                                        )
                                        .build()
                        )
                        .build()
        );
        TranslationMap newMap = new TranslationMap(
                "1",
                BundleCollectionBuilder.create()
                        .bundle(
                                BundleBuilder.create("common")
                                        .section(
                                                BundleSectionBuilder
                                                        .create(Bundle.DEFAULT_SECTION)
                                                        .key(
                                                                BundleKeyBuilder.create("added.default-only")
                                                                        .addValue(Locale.FRENCH, "Ajout du défaut")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("updated.default-only")
                                                                        .addValue(Locale.FRENCH, "Mise à jour du défaut")
                                                        )
                                        )
                                        .build()
                        )
                        .build()
        );
        // Merge
        TranslationMap map = oldMap.merge(newMap);
        // Check
        String json = objectMapper.writeValueAsString(map);
        assertEquals("{\"version\":\"1\",\"bundleCollection\":{\"bundles\":[{\"name\":\"common\",\"comments\":[],\"sections\":[{\"name\":\"default\",\"comments\":[],\"keys\":[{\"name\":\"added.default-only\",\"comments\":[],\"values\":{\"en\":{\"comments\":[],\"value\":\"Added default only\"},\"fr\":{\"comments\":[],\"value\":\"Ajout du défaut\"}}},{\"name\":\"added.both\",\"comments\":[],\"values\":{\"en\":{\"comments\":[],\"value\":\"Added both\"},\"fr\":{\"comments\":[],\"value\":\"Ajout des deux\"}}},{\"name\":\"updated.default-only\",\"comments\":[],\"values\":{\"en\":{\"comments\":[],\"value\":\"Updated default only\"},\"fr\":{\"comments\":[],\"value\":\"Mise à jour du défaut\"}}},{\"name\":\"updated.both\",\"comments\":[],\"values\":{\"en\":{\"comments\":[],\"value\":\"Updated both\"},\"fr\":{\"comments\":[],\"value\":\"Les deux sont modifiés\"}}},{\"name\":\"deleted.default-only\",\"comments\":[],\"values\":{\"fr\":{\"comments\":[],\"value\":\"Valeur initiale\"}}},{\"name\":\"unchanged\",\"comments\":[],\"values\":{\"en\":{\"comments\":[],\"value\":\"Unchanged\"},\"fr\":{\"comments\":[],\"value\":\"Inchangé\"}}}]}]}]},\"supportedLocales\":[\"en\",\"fr\"]}", json);
    }

}
