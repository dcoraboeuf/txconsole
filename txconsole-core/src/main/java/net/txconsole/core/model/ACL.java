package net.txconsole.core.model;

import lombok.Data;
import net.txconsole.core.security.SecurityCategory;

@Data
public class ACL {

    private final SecurityCategory category;
    private final int id;
    private final String action;

}
