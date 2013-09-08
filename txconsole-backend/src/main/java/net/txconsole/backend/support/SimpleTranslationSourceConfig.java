package net.txconsole.backend.support;

import lombok.Data;
import net.txconsole.service.support.Configured;
import net.txconsole.service.support.TxFileFormat;
import net.txconsole.service.support.TxFileSource;

@Data
public class SimpleTranslationSourceConfig<S, F> {

    private final Configured<S, TxFileSource<S>> txFileSourceConfigured;
    private final Configured<F, TxFileFormat<F>> txFileFormatConfigured;

}
