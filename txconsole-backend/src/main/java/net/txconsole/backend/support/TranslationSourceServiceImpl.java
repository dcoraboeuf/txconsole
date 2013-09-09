package net.txconsole.backend.support;

import net.txconsole.service.support.TranslationSource;
import net.txconsole.service.support.TranslationSourceService;
import net.txconsole.service.support.TxFileFormat;
import net.txconsole.service.support.TxFileSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TranslationSourceServiceImpl implements TranslationSourceService {

    private final Collection<TranslationSource<?>> sources;
    private final Collection<TxFileSource<?>> txFileSources;
    private final Collection<TxFileFormat<?>> txFileFormats;

    @Autowired
    public TranslationSourceServiceImpl(
            Collection<TranslationSource<?>> sources,
            Collection<TxFileSource<?>> txFileSources,
            Collection<TxFileFormat<?>> txFileFormats) {
        this.sources = sources;
        this.txFileSources = txFileSources;
        this.txFileFormats = txFileFormats;
    }

    @Override
    public Collection<TranslationSource<?>> getTranslationSourceList() {
        return sources;
    }

    @Override
    public Collection<TxFileSource<?>> getTxFileSourceList() {
        return txFileSources;
    }

    @Override
    public Collection<TxFileFormat<?>> getTxFileFormatList() {
        return txFileFormats;
    }
}
