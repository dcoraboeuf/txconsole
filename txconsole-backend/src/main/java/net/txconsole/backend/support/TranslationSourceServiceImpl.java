package net.txconsole.backend.support;

import com.google.common.collect.Maps;
import net.txconsole.backend.exceptions.TranslationSourceConfigIOException;
import net.txconsole.backend.exceptions.TranslationSourceIDException;
import net.txconsole.core.model.JsonConfiguration;
import net.txconsole.service.support.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

@Service
public class TranslationSourceServiceImpl implements TranslationSourceService {

    private final ObjectMapper objectMapper;
    private final Map<String, TranslationSource<?>> sources;
    private final Collection<TxFileSource<?>> txFileSources;
    private final Collection<TxFileFormat<?>> txFileFormats;

    @Autowired
    public TranslationSourceServiceImpl(
            ObjectMapper objectMapper,
            Collection<TranslationSource<?>> sources,
            Collection<TxFileSource<?>> txFileSources,
            Collection<TxFileFormat<?>> txFileFormats) {
        this.objectMapper = objectMapper;
        this.sources = Maps.uniqueIndex(sources, Descriptible.idFn);
        this.txFileSources = txFileSources;
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
        // FIXME Reads the configuration
        C configuration;
        try {
            configuration = (C) objectMapper.readValue(config.getNode(), translationSource.getConfigClass());
        } catch (IOException e) {
            throw new TranslationSourceConfigIOException(config.getId(), e);
        }
        // OK
        return new Configured<>(configuration, translationSource);
    }
}
