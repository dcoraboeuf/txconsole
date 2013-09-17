package net.txconsole.core.model;

import lombok.Data;

@Data
public class RequestView {

    private final RequestSummary summary;
    private final TranslationDiff diff;

}
