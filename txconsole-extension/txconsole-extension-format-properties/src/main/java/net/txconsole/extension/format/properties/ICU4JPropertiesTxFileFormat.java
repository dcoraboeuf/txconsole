package net.txconsole.extension.format.properties;

import net.txconsole.service.support.NOPEscapingService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ICU4JPropertiesTxFileFormat extends PropertiesTxFileFormat {

    @Autowired
    public ICU4JPropertiesTxFileFormat(ObjectMapper objectMapper) {
        super(
                "extension-txfileformat-properties-icu4j",
                "extension.format.properties.icu4j",
                "extension.format.properties.icu4j.description",
                PropertiesTxFileFormatConfig.class,
                objectMapper,
                new NOPEscapingService());
    }
}
