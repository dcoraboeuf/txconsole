package net.txconsole.service.support;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JDKEscapingServiceTest {

    public static final String HUMAN_VALUE = "'At start, valid '' and middle 'ONE','TWO','THREE' and 'END'";
    public static final String PROPERTY_VALUE = "''At start, valid '''' and middle ''ONE'',''TWO'',''THREE'' and ''END''";
    private final JDKEscapingService service = new JDKEscapingService();

    @Test
    public void write_apos() {
        assertEquals(
                PROPERTY_VALUE,
                service.write(HUMAN_VALUE)
        );
    }

    @Test
    public void read_apos() {
        assertEquals(
                HUMAN_VALUE,
                service.read(PROPERTY_VALUE)
        );
    }

}
