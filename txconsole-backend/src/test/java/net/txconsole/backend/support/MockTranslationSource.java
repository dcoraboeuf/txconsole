package net.txconsole.backend.support;

import net.txconsole.core.RunProfile;
import net.txconsole.core.model.TranslationMap;
import net.txconsole.service.support.AbstractConfigurable;
import net.txconsole.service.support.TranslationSource;
import net.txconsole.service.support.VersionFormat;
import org.codehaus.jackson.JsonNode;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Profile(RunProfile.TEST)
public class MockTranslationSource
        extends AbstractConfigurable<MockTranslationSourceConfig>
        implements TranslationSource<MockTranslationSourceConfig> {

    public MockTranslationSource() {
        super("mock", "txsource.mock", "txsource.mock.description", MockTranslationSourceConfig.class);
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
    public MockTranslationSourceConfig readConfiguration(JsonNode node) throws IOException {
        return new MockTranslationSourceConfig();
    }
}
