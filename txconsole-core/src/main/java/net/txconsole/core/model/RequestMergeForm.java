package net.txconsole.core.model;

import lombok.Data;

@Data
public class RequestMergeForm {

    private final boolean force;
    private final String message;

}
