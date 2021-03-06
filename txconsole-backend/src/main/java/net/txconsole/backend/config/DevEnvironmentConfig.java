package net.txconsole.backend.config;

import net.txconsole.core.RunProfile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.File;

@Configuration
@Profile({RunProfile.DEV})
public class DevEnvironmentConfig extends AbstractEnvironmentConfig {

    @Override
    @Bean
    public File homeDir() {
        return new File("work/root");
    }
}
