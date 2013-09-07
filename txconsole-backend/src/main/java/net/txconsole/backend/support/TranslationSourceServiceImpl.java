package net.txconsole.backend.support;

import net.txconsole.service.support.TranslationSource;
import net.txconsole.service.support.TranslationSourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class TranslationSourceServiceImpl implements TranslationSourceService {

    private final Collection<TranslationSource<?>> sources;

    @Autowired
    public TranslationSourceServiceImpl(Collection<TranslationSource<?>> sources) {
        this.sources = sources;
    }

    @Override
    public Collection<TranslationSource<?>> getTranslationSourceList() {
        return sources;
    }
}
