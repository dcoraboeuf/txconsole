package net.txconsole.core.model;

import lombok.Data;

/**
 * Summary of data for a branch.
 */
@Data
public class BranchDashboard {

    private final BranchSummary branch;
    private final RequestSummary lastOpenRequest;

}
