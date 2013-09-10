package net.txconsole.backend.dao.model;

import lombok.Data;

import java.util.Map;

@Data
public class TBranch {

    private final int id;
    private final int project;
    private final String name;
    // TODO private final Map<String, String> parameters;
}
