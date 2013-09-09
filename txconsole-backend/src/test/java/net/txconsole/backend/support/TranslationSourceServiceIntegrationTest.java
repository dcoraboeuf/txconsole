package net.txconsole.backend.support;

import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TranslationSource;
import net.txconsole.service.support.TranslationSourceService;
import net.txconsole.test.AbstractIntegrationTest;
import net.txconsole.test.Helper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class TranslationSourceServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TranslationSourceService translationSourceService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getConfiguredTranslationSource_ok() throws IOException {
        JsonNode json = objectMapper.readTree(Helper.getResourceAsString(getClass(), "txSourceConfiguration.json"));
        Configured<Object, TranslationSource<Object>> translationSource = translationSourceService.getConfiguredTranslationSource(
                new JsonConfiguration(
                        "simple",
                        json
                )
        );
        assertNotNull(translationSource);
    }

}
