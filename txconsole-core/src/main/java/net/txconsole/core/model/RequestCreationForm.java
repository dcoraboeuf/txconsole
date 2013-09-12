package net.txconsole.core.model;

import lombok.Data;

@Data
public class RequestCreationForm {

    private final String version;
    private final JsonConfiguration txFileExchangeConfig;

}
