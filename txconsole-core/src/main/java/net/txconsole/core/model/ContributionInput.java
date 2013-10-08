package net.txconsole.core.model;

import lombok.Data;

import java.util.Collection;

@Data
public class ContributionInput {

    private final int id;
    private final String message;
    private final Collection<ContributionEntry> contributions;

}
