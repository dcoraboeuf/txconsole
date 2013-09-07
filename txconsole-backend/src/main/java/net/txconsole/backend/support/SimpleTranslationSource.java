package net.txconsole.backend.support;

import net.txconsole.core.model.TranslationMap;
import net.txconsole.service.support.AbstractConfigurable;
import net.txconsole.service.support.FileSource;
import net.txconsole.service.support.TranslationSource;
import org.springframework.stereotype.Component;

@Component
public class SimpleTranslationSource extends AbstractConfigurable<SimpleTranslationSourceConfig> implements TranslationSource<SimpleTranslationSourceConfig> {

    public SimpleTranslationSource() {
        super(
                "simple",
                "txsource.simple",
                "txsource.simple.description",
                SimpleTranslationSourceConfig.class);
    }

    @Override
    public TranslationMap read() {
        // TODO Sync (transaction callback)
        // Gets the file source
        FileSource s = getConfig().getSource().getSource();
        // Reads the map
        return getConfig().getFormat().readFrom(s);
    }

    @Override
    public void write(TranslationMap map) {
        // TODO Sync (transaction callback)
        // Gets the file source
        FileSource s = getConfig().getSource().getSource();
        // Writes the map
        getConfig().getFormat().writeTo(map, s);
    }
}
