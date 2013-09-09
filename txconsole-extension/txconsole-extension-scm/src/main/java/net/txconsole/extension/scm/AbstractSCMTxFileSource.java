package net.txconsole.extension.scm;

import net.txconsole.service.support.AbstractSimpleConfigurable;
import net.txconsole.service.support.TxFileSource;
import org.codehaus.jackson.map.ObjectMapper;

public abstract class AbstractSCMTxFileSource<C> extends AbstractSimpleConfigurable<C> implements TxFileSource<C> {

    public AbstractSCMTxFileSource(String id, String nameKey, String descriptionKey, Class<C> configClass, ObjectMapper objectMapper) {
        super(id, nameKey, descriptionKey, configClass, objectMapper);
    }

}
