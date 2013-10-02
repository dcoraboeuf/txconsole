package net.txconsole.core.model;

import lombok.Data;

@Data
public class ProjectAuthorization {

    private final int project;
    private final AccountSummary account;
    private final ProjectRole role;

}
