package net.txconsole.core.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class ContributionSummary {

    private final int id;
    private final boolean direct;
    private final int branch;
    private final String message;
    private final AccountSummary author;
    private final DateTime timestamp;

}
