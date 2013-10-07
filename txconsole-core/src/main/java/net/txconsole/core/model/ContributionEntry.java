package net.txconsole.core.model;

import lombok.Data;

import java.util.Locale;

@Data
public class ContributionEntry {

    private final String bundle;
    private final String section;
    private final String key;
    private final Locale locale;
    private final String value;

}
