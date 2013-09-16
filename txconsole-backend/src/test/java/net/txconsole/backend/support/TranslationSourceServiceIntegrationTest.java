package net.txconsole.backend.support;

import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.extension.format.properties.PropertiesTxFileFormat;
import net.txconsole.extension.format.properties.PropertiesTxFileFormatConfig;
import net.txconsole.extension.format.properties.PropertyGroup;
import net.txconsole.extension.svn.SVNTxFileSource;
import net.txconsole.extension.svn.SVNTxFileSourceConfig;
import net.txconsole.service.support.*;
import net.txconsole.test.AbstractIntegrationTest;
import net.txconsole.test.Helper;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class TranslationSourceServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private TranslationSourceService translationSourceService;
    @Autowired
    private SVNTxFileSource svnTxFileSource;
    @Autowired
    private PropertiesTxFileFormat propertiesTxFileFormat;
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
        assertTrue(translationSource.getConfigurable() instanceof SimpleTranslationSource);
        assertEquals(
                new SimpleTranslationSourceConfig<>(
                        new Configured<SVNTxFileSourceConfig, TxFileSource<SVNTxFileSourceConfig>>(
                                new SVNTxFileSourceConfig(
                                        "http://test/project/translations",
                                        "translator",
                                        "xxx"
                                ),
                                svnTxFileSource
                        ),
                        new Configured<PropertiesTxFileFormatConfig, TxFileFormat<PropertiesTxFileFormatConfig>>(
                                new PropertiesTxFileFormatConfig(
                                        Locale.ENGLISH,
                                        asList(
                                                new PropertyGroup("common", asList(Locale.ENGLISH, Locale.FRENCH)),
                                                new PropertyGroup("lux", asList(Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN))
                                        )
                                ),
                                propertiesTxFileFormat
                        )
                ),
                translationSource.getConfiguration());
    }

}
