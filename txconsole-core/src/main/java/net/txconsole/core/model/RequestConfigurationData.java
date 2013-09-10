package net.txconsole.core.model;

import lombok.Data;

@Data
public class RequestConfigurationData {

    private final ProjectSummary project;
    private final BranchSummary branch;
    private final VersionFormat versionFormat;
    private final String lastVersion;

}
