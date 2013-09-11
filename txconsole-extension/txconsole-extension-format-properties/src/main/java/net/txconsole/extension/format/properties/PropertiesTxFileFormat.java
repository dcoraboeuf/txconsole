package net.txconsole.extension.format.properties;

import net.txconsole.core.model.TranslationMap;
import net.txconsole.service.support.AbstractSimpleConfigurable;
import net.txconsole.service.support.IOContext;
import net.txconsole.service.support.TxFileFormat;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropertiesTxFileFormat extends AbstractSimpleConfigurable<PropertiesTxFileFormatConfig> implements TxFileFormat<PropertiesTxFileFormatConfig> {

    @Autowired
    public PropertiesTxFileFormat(ObjectMapper objectMapper) {
        super(
                "extension-txfileformat-properties",
                "extension.format.properties",
                "extension.format.properties.description",
                PropertiesTxFileFormatConfig.class, objectMapper);
    }

    @Override
    public TranslationMap readFrom(PropertiesTxFileFormatConfig config, IOContext context) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeTo(PropertiesTxFileFormatConfig config, TranslationMap map, IOContext context) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
