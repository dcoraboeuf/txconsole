package net.txconsole.core.model;

import lombok.Data;

@Data
public class TranslationMapRequest {

    private final int limit;
    private final String filter;
    private final String version;

}
