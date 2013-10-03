package net.txconsole.backend.config;

import net.sf.jstring.Strings;
import net.txconsole.core.RunProfile;
import net.txconsole.core.support.MapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jmx.export.MBeanExporter;

import java.io.IOException;

@Configuration
@Profile({RunProfile.DEV, RunProfile.IT, RunProfile.PROD})
public class JMXConfiguration {

    @Autowired
    private Strings strings;

    @Bean
    public Object exporter() throws IOException {
        MBeanExporter exporter = new MBeanExporter();
        exporter.setBeans(MapBuilder.<String, Object>create()
                .with("configuration:app=txconsole,name=strings", strings)
                .get());
        return exporter;
    }

}
