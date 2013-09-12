package net.txconsole.backend.support;

import net.txconsole.backend.config.EnvironmentConfig;
import net.txconsole.backend.exceptions.IOContextException;
import net.txconsole.service.support.IOContext;
import net.txconsole.service.support.IOContextFactory;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static java.lang.String.format;

@Component
public class IOContextFactoryImpl implements IOContextFactory {

    private final EnvironmentConfig environmentConfig;

    @Autowired
    public IOContextFactoryImpl(EnvironmentConfig environmentConfig) {
        this.environmentConfig = environmentConfig;
    }

    @Override
    public IOContext createContext(String category) {
        return createContextWithName(format("%s-%s", category, UUID.randomUUID()));

    }

    @Override
    public IOContext getOrCreateContext(String category, String idInCategory) {
        return createContextWithName(format("%s-%s", category, idInCategory));
    }

    protected IOContext createContextWithName(String name) {
        // Directory
        File dir = new File(environmentConfig.homeDir(), name);
        // Creates it
        try {
            FileUtils.forceMkdir(dir);
        } catch (IOException e) {
            throw new IOContextException(e);
        }
        // OK
        return new DirIOContext(dir);
    }

}
