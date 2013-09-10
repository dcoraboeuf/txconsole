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

}
