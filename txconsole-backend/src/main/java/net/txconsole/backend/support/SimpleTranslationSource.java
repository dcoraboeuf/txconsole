package net.txconsole.backend.support;

import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.core.model.TranslationMap;
import net.txconsole.core.model.VersionFormat;
import net.txconsole.core.support.MapBuilder;
import net.txconsole.service.support.AbstractConfigurable;
import net.txconsole.service.support.IOContext;
import net.txconsole.service.support.TranslationSource;
import net.txconsole.service.support.TranslationSourceService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SimpleTranslationSource<S, F> extends AbstractConfigurable<SimpleTranslationSourceConfig<S, F>> implements TranslationSource<SimpleTranslationSourceConfig<S, F>> {

    private final TranslationSourceService translationSourceService;
    private final ObjectMapper objectMapper;

    @Autowired
    public SimpleTranslationSource(TranslationSourceService translationSourceService, ObjectMapper objectMapper) {
        super(
                "simple",
                "txsource.simple",
                "txsource.simple.description",
                SimpleTranslationSourceConfig.class);
        this.translationSourceService = translationSourceService;
        this.objectMapper = objectMapper;
    }

    @Override
    public VersionFormat getVersionSemantics(SimpleTranslationSourceConfig<S, F> config) {
        return config.getTxFileSourceConfigured().getConfigurable().getVersionSemantics();
    }

    @Override
    public TranslationMap read(SimpleTranslationSourceConfig<S, F> config, String version) {
        // TODO Sync (transaction callback)
        // Gets the file source
        IOContext s = config.getTxFileSourceConfigured().getConfigurable().getSource(config.getTxFileSourceConfigured().getConfiguration(), version);
        // Reads the map
        return config.getTxFileFormatConfigured().getConfigurable().readFrom(
                config.getTxFileFormatConfigured().getConfiguration(),
                s);
    }

    @Override
    public void write(SimpleTranslationSourceConfig<S, F> config, TranslationMap map) {
        // TODO Sync (transaction callback)
        // Gets the file source
        IOContext s = config.getTxFileSourceConfigured().getConfigurable().getSource(config.getTxFileSourceConfigured().getConfiguration(), null);
        // Writes the map
        config.getTxFileFormatConfigured().getConfigurable().writeTo(
                config.getTxFileFormatConfigured().getConfiguration(),
                map,
                s
        );
    }

    @Override
    public SimpleTranslationSourceConfig<S, F> readConfiguration(JsonNode node) {
        return new SimpleTranslationSourceConfig<>(
                translationSourceService.<S>getConfiguredTxFileSource(
                        JsonConfiguration.fromJson(node, "txFileSourceConfigured")
                ),
                translationSourceService.<F>getConfiguredTxFileFormat(
                        JsonConfiguration.fromJson(node, "txFileFormatConfigured")
                )
        );
    }

    @Override
    public SimpleTranslationSourceConfig<S, F> readConfiguration(String json) throws IOException {
        return readConfiguration(objectMapper.readTree(json));
    }

    @Override
    public JsonNode writeConfiguration(SimpleTranslationSourceConfig<S, F> config) throws IOException {
        return objectMapper.valueToTree(
                MapBuilder
                        .of(
                                "txFileSourceConfigured",
                                config.getTxFileSourceConfigured().getJsonConfiguration())
                        .with(
                                "txFileFormatConfigured",
                                config.getTxFileFormatConfigured().getJsonConfiguration())
                        .get()
        );
    }

    @Override
    public String writeConfigurationAsJsonString(SimpleTranslationSourceConfig<S, F> config) throws IOException {
        return objectMapper.writeValueAsString(writeConfiguration(config));
    }
}
