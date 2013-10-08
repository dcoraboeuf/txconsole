package net.txconsole.core.model;

import lombok.Data;

import java.util.Locale;

@Data
public class ContributionDetail {

    private final String bundle;
    private final String section;
    private final String key;
    private final Locale locale;
    private final String oldValue;
    private final String newValue;

}
