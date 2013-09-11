package net.txconsole.core.model;

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
        TranslationMap map = new TranslationMap();
        map.insert(TranslationKey.key("one"), Locale.ENGLISH, "One");
        map.insert(TranslationKey.key("one"), Locale.FRENCH, "Un");
        map.insert(TranslationKey.key("one"), Locale.GERMAN, "Eins");
        map.insert(TranslationKey.key("two"), Locale.ENGLISH, "Two");
        map.insert(TranslationKey.key("two"), Locale.FRENCH, "Deux");
        map.insert(TranslationKey.key("two"), Locale.GERMAN, "Zwei");
        String json = objectMapper.writeValueAsString(map);
        assertEquals("", json);
    }

}
