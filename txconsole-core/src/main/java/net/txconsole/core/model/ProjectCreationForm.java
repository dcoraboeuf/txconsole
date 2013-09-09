package net.txconsole.core.model;

import lombok.Data;

import java.util.List;

@Data
public class ProjectCreationForm {

    private final String name;
    private final String fullName;
    private final List<String> languages;
    private final JsonConfiguration txSourceConfig;

}
