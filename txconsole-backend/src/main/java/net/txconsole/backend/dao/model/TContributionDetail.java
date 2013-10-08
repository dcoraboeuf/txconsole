package net.txconsole.backend.dao.model;

import lombok.Data;

import java.util.Locale;

@Data
public class TContributionDetail {

    private final int id;
    private final int contribution;
    private final String bundle;
    private final String section;
    private final String key;
    private final Locale locale;
    private final String oldValue;
    private final String newValue;
}
