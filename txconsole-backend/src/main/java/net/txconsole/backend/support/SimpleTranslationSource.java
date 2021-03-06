package net.txconsole.backend.support;

import com.google.common.base.Function;
import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.core.model.TranslationMap;
import net.txconsole.core.model.VersionFormat;
import net.txconsole.core.support.IOContext;
import net.txconsole.core.support.MapBuilder;
import net.txconsole.service.support.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;

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
    public TranslationMap read(final SimpleTranslationSourceConfig<S, F> config, String version) {
        return config.getTxFileSourceConfigured().getConfigurable().withReadableSource(
                config.getTxFileSourceConfigured().getConfiguration(),
                version,
                new Function<IOContext, TranslationMap>() {
                    @Override
                    public TranslationMap apply(IOContext input) {
                        return config.getTxFileFormatConfigured().getConfigurable().readFrom(
                                config.getTxFileFormatConfigured().getConfiguration(),
                                input);
                    }
                }
        ).getData();
    }

    @Override
    public String write(SimpleTranslationSourceConfig<S, F> config, final TranslationMap map, String message) {
        // Gets the configuration
        TxFileSource<S> fileSource = config.getTxFileSourceConfigured().getConfigurable();
        S fileSourceConfig = config.getTxFileSourceConfigured().getConfiguration();
        final TxFileFormat<F> fileFormat = config.getTxFileFormatConfigured().getConfigurable();
        final F fileFormatConfig = config.getTxFileFormatConfigured().getConfiguration();
        // Writes
        return fileSource.withWritableSource(
                fileSourceConfig,
                null,
                message,
                new Function<IOContext, Void>() {
                    @Override
                    public Void apply(IOContext ioContext) {
                        fileFormat.writeTo(
                                fileFormatConfig,
                                map,
                                ioContext
                        );
                        return null;
                    }
                }
        ).getVersion();
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

    @Override
    public Locale getDefaultLocale(SimpleTranslationSourceConfig<S, F> configuration) {
        return configuration.getTxFileFormatConfigured().getConfigurable().getDefaultLocale(configuration.getTxFileFormatConfigured().getConfiguration());
    }

    @Override
    public Set<Locale> getSupportedLocales(SimpleTranslationSourceConfig<S, F> configuration) {
        return configuration.getTxFileFormatConfigured().getConfigurable().getSupportedLocales(configuration.getTxFileFormatConfigured().getConfiguration());
    }
}
