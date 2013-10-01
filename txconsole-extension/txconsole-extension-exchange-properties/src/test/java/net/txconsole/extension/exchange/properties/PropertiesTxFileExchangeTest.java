package net.txconsole.extension.exchange.properties;

import com.google.common.collect.Sets;
import com.netbeetle.jackson.ObjectMapperFactory;
import net.txconsole.core.Content;
import net.txconsole.service.support.DefaultEscapingService;
import net.txconsole.test.DirTestIOContextFactory;
import net.txconsole.test.Helper;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PropertiesTxFileExchangeTest {

    @Test
    public void export() throws IOException {
        PropertiesTxFileExchange exchange = new PropertiesTxFileExchange(
                ObjectMapperFactory.createObjectMapper(),
                new DirTestIOContextFactory(),
                new DefaultEscapingService()
        );
        Content content = exchange.export(
                new PropertiesTxFileExchangeConfig(),
                Locale.ENGLISH,
                Sets.newHashSet(Locale.ENGLISH, Locale.FRENCH),
                Helper.sampleDiff()
        );
        assertNotNull(content);
        assertEquals("application/zip", content.getType());
        // TODO Unzips the content
    }

}
