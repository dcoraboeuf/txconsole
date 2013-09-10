package net.txconsole.core.model;

import lombok.Data;

import java.util.Collection;

@Data
public class BranchCreationForm {

    private final String name;
    private final Collection<ParameterValueForm> parameters;

}
