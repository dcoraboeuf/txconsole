package net.txconsole.extension.scm;

import net.txconsole.service.support.AbstractConfigurable;
import net.txconsole.service.support.TxFileSource;

public abstract class AbstractSCMTxFileSource<C> extends AbstractConfigurable<C> implements TxFileSource<C> {

    public AbstractSCMTxFileSource(String id, String nameKey, String descriptionKey, Class<C> configClass) {
        super(id, nameKey, descriptionKey, configClass);
    }

}
