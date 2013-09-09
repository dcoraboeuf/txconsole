package net.txconsole.extension.format.properties;

import net.txconsole.core.model.TranslationMap;
import net.txconsole.service.support.AbstractConfigurable;
import net.txconsole.service.support.FileSource;
import net.txconsole.service.support.TxFileFormat;
import org.springframework.stereotype.Component;

@Component
public class PropertiesTxFileFormat extends AbstractConfigurable<PropertiesTxFileFormatConfig> implements TxFileFormat<PropertiesTxFileFormatConfig> {

    public PropertiesTxFileFormat() {
        super(
                "txfileformat-properties",
                "extension.format.properties",
                "extension.format.properties.description",
                PropertiesTxFileFormatConfig.class);
    }

    @Override
    public TranslationMap readFrom(PropertiesTxFileFormatConfig config, FileSource source) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeTo(PropertiesTxFileFormatConfig config, TranslationMap map, FileSource source) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
