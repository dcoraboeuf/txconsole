package net.txconsole.backend.dao.model;

import lombok.Data;
import net.txconsole.core.model.JsonConfiguration;

import java.util.List;
import java.util.Locale;

@Data
public class TProject {

    private final int id;
    private final String name;
    private final String fullName;
    private final JsonConfiguration txSourceConfig;
}
