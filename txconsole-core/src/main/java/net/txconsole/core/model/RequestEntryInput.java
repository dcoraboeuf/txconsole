package net.txconsole.core.model;

import lombok.Data;

import java.util.Locale;

@Data
public class RequestEntryInput {

    private final Locale locale;
    private final String value;

}
