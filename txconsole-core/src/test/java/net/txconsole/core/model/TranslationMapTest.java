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

}
