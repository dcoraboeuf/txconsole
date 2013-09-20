package net.txconsole.backend.config;

import net.sf.jstring.Strings;
import net.txconsole.backend.task.IRequestCreationBatch;
import net.txconsole.core.support.MapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.MBeanExporter;

import java.io.IOException;

@Configuration
public class JMXConfiguration {

    @Autowired
    private IRequestCreationBatch requestCreationBatch;
    @Autowired
    private Strings strings;

    @Bean
    public Object exporter() throws IOException {
        MBeanExporter exporter = new MBeanExporter();
        exporter.setBeans(MapBuilder.<String, Object>create()
                .with("batch:name=requestCreationBatch", requestCreationBatch)
                .with("configuration:name=strings", strings)
                .get());
        return exporter;
    }

}
