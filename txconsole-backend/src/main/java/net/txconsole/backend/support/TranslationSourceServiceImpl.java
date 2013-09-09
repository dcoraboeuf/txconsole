package net.txconsole.backend.support;

import com.google.common.collect.Maps;
import net.txconsole.backend.exceptions.ConfigIDException;
import net.txconsole.backend.exceptions.ConfigIOException;
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
    private Map<String, TxFileSource<?>> txFileSources;
    private Map<String, TxFileFormat<?>> txFileFormats;

    @Autowired
    public void setSources(Collection<TranslationSource<?>> sources) {
        this.sources = Maps.uniqueIndex(sources, Descriptible.idFn);
    }

    @Autowired
    public void setTxFileSources(Collection<TxFileSource<?>> txFileSources) {
        this.txFileSources = Maps.uniqueIndex(txFileSources, Descriptible.idFn);
    }

    @Autowired
    public void setTxFileFormats(Collection<TxFileFormat<?>> txFileFormats) {
        this.txFileFormats = Maps.uniqueIndex(txFileFormats, Descriptible.idFn);
    }

    @Override
    public Collection<TranslationSource<?>> getTranslationSourceList() {
        return sources.values();
    }

    @Override
    public Collection<TxFileSource<?>> getTxFileSourceList() {
        return txFileSources.values();
    }

    @Override
    public Collection<TxFileFormat<?>> getTxFileFormatList() {
        return txFileFormats.values();
    }

    protected <C, T extends Configurable<C>> Configured<C, T> getConfigured(String configType, Map<String, ? extends Configurable<?>> configMap, JsonConfiguration config) {
        // Gets the source class
        @SuppressWarnings("unchecked")
        T configurable = (T) configMap.get(config.getId());
        if (configurable == null) {
            throw new ConfigIDException(configType, config.getId());
        }
        // Reads the configuration
        C configuration;
        try {
            configuration = configurable.readConfiguration(config.getNode());
        } catch (IOException e) {
            throw new ConfigIOException(configType, config.getId(), e);
        }
        // OK
        return new Configured<>(configuration, configurable);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <C> Configured<C, TranslationSource<C>> getConfiguredTranslationSource(JsonConfiguration config) {
        return getConfigured("txsource", sources, config);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S> Configured<S, TxFileSource<S>> getConfiguredTxFileSource(JsonConfiguration config) {
        return getConfigured("txfilesource", txFileSources, config);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <F> Configured<F, TxFileFormat<F>> getConfiguredTxFileFormat(JsonConfiguration config) {
        return getConfigured("txfileformat", txFileFormats, config);
    }
}
