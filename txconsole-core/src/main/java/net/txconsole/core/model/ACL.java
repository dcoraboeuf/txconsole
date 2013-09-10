package net.txconsole.core.model;

import lombok.Data;
import net.txconsole.core.security.SecurityFunction;

@Data
public class ACL {

    private final SecurityFunction fn;
    private final int id;

}
