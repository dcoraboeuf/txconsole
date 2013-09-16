package net.txconsole.backend.support;

import net.txconsole.core.RunProfile;
import net.txconsole.core.model.TranslationMap;
import net.txconsole.core.model.VersionFormat;
import net.txconsole.service.support.AbstractSimpleConfigurable;
import net.txconsole.service.support.TranslationSource;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

@Component
@Profile(RunProfile.TEST)
public class MockTranslationSource
        extends AbstractSimpleConfigurable<MockTranslationSourceConfig>
        implements TranslationSource<MockTranslationSourceConfig> {

    @Autowired
    public MockTranslationSource(ObjectMapper objectMapper) {
        super("mock", "txsource.mock", "txsource.mock.description", MockTranslationSourceConfig.class, objectMapper);
    }

    @Override
    public TranslationMap read(MockTranslationSourceConfig config, String version) {
        return null;
    }

    @Override
    public VersionFormat getVersionSemantics(MockTranslationSourceConfig config) {
        return new VersionFormat(
                "",
                ".*"
        );
    }

    @Override
    public void write(MockTranslationSourceConfig config, TranslationMap map) {
    }

    @Override
    public Locale getDefaultLocale(MockTranslationSourceConfig configuration) {
        return Locale.ENGLISH;
    }

    @Override
    public Set<Locale> getSupportedLocales(MockTranslationSourceConfig configuration) {
        return Collections.singleton(Locale.ENGLISH);
    }
}
