package net.txconsole.backend.support;

import com.google.common.collect.Maps;
import net.txconsole.backend.exceptions.TranslationSourceConfigIOException;
import net.txconsole.backend.exceptions.TranslationSourceIDException;
import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.service.support.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Service
public class TranslationSourceServiceImpl implements TranslationSourceService {

    private Map<String, TranslationSource<?>> sources;
    private Collection<TxFileSource<?>> txFileSources;
    private Collection<TxFileFormat<?>> txFileFormats;

    @Autowired
    public void setSources(Collection<TranslationSource<?>> sources) {
        this.sources = Maps.uniqueIndex(sources, Descriptible.idFn);
    }

    @Autowired
    public void setTxFileSources(Collection<TxFileSource<?>> txFileSources) {
        this.txFileSources = txFileSources;
    }

    @Autowired
    public void setTxFileFormats(Collection<TxFileFormat<?>> txFileFormats) {
        this.txFileFormats = txFileFormats;
    }

    @Override
    public Collection<TranslationSource<?>> getTranslationSourceList() {
        return sources.values();
    }

    @Override
    public Collection<TxFileSource<?>> getTxFileSourceList() {
        return txFileSources;
    }

    @Override
    public Collection<TxFileFormat<?>> getTxFileFormatList() {
        return txFileFormats;
    }

    @Override
    public <C> Configured<C, TranslationSource<C>> getConfiguredTranslationSource(JsonConfiguration config) {
        // Gets the source class
        @SuppressWarnings("unchecked")
        TranslationSource<C> translationSource = (TranslationSource<C>) sources.get(config.getId());
        if (translationSource == null) {
            throw new TranslationSourceIDException(config.getId());
        }
        // Reads the configuration
        C configuration;
        try {
            configuration = translationSource.readConfiguration(config.getNode());
        } catch (IOException e) {
            throw new TranslationSourceConfigIOException(config.getId(), e);
        }
        // OK
        return new Configured<>(configuration, translationSource);
    }

    @Override
    public <S> Configured<S, TxFileSource<S>> getConfiguredTxFileSource(JsonConfiguration config) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <F> Configured<F, TxFileFormat<F>> getConfiguredTxFileFormat(JsonConfiguration config) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
