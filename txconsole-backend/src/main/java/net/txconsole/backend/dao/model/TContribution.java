package net.txconsole.backend.dao.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class TContribution {

    private final int id;
    private final int branch;
    private final String message;
    private final int author;
    private final DateTime timestamp;

}
