package net.txconsole.backend.support;

import lombok.Data;
import net.txconsole.service.support.TxFileFormat;
import net.txconsole.service.support.TxFileSource;

@Data
public class SimpleTranslationSourceConfig<S, F> {

    private final TxFileSource<S> source;
    private final TxFileFormat<F> format;

}
