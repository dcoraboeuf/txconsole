package net.txconsole.backend.support;

import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.core.model.TranslationMap;
import net.txconsole.service.support.*;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SimpleTranslationSource<S, F> extends AbstractConfigurable<SimpleTranslationSourceConfig<S, F>> implements TranslationSource<SimpleTranslationSourceConfig<S, F>> {

    private final TranslationSourceService translationSourceService;

    @Autowired
    public SimpleTranslationSource(TranslationSourceService translationSourceService) {
        super(
                "simple",
                "txsource.simple",
                "txsource.simple.description",
                SimpleTranslationSourceConfig.class);
        this.translationSourceService = translationSourceService;
    }

    @Override
    public VersionFormat getVersionSemantics(SimpleTranslationSourceConfig<S, F> config) {
        return config.getTxFileSourceConfigured().getConfigurable().getVersionSemantics();
    }

    @Override
    public TranslationMap read(SimpleTranslationSourceConfig<S, F> config, String version) {
        // TODO Sync (transaction callback)
        // Gets the file source
        FileSource s = config.getTxFileSourceConfigured().getConfigurable().getSource(config.getTxFileSourceConfigured().getConfiguration(), version);
        // Reads the map
        return config.getTxFileFormatConfigured().getConfigurable().readFrom(
                config.getTxFileFormatConfigured().getConfiguration(),
                s);
    }

    @Override
    public void write(SimpleTranslationSourceConfig<S, F> config, TranslationMap map) {
        // TODO Sync (transaction callback)
        // Gets the file source
        FileSource s = config.getTxFileSourceConfigured().getConfigurable().getSource(config.getTxFileSourceConfigured().getConfiguration(), null);
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

}
