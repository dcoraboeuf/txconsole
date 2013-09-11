package net.txconsole.extension.exchange.properties;

import net.txconsole.service.support.AbstractSimpleConfigurable;
import net.txconsole.service.support.TxFileExchange;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PropertiesTxFileExchange extends AbstractSimpleConfigurable<PropertiesTxFileExchangeConfig> implements TxFileExchange<PropertiesTxFileExchangeConfig> {

    @Autowired
    public PropertiesTxFileExchange(ObjectMapper objectMapper) {
        super(
                "extension-txfileexchange-properties",
                "extension.txfileexchange.properties",
                "extension.txfileexchange.properties.description",
                PropertiesTxFileExchangeConfig.class,
                objectMapper);
    }

}
