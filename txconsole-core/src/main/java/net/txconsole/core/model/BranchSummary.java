package net.txconsole.core.model;

import lombok.Data;

@Data
public class BranchSummary {

    private final int id;
    private final int projectId;
    private final String name;

}
