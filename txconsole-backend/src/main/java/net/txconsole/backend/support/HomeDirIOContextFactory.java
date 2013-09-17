package net.txconsole.backend.support;

import net.txconsole.backend.config.EnvironmentConfig;
import net.txconsole.core.support.DirIOContextFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class HomeDirIOContextFactory extends DirIOContextFactory {

    private final EnvironmentConfig environmentConfig;

    @Autowired
    public HomeDirIOContextFactory(EnvironmentConfig environmentConfig) {
        this.environmentConfig = environmentConfig;
    }

    @Override
    protected File getRootDir() {
        return environmentConfig.homeDir();
    }
}
