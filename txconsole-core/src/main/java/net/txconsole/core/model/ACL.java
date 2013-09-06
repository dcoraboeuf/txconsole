package net.txconsole.core.model;

import lombok.Data;

@Data
public class ACL {

    private final String category;
    private final int id;
    private final String action;

}
