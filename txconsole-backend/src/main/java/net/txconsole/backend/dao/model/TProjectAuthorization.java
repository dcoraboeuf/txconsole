package net.txconsole.backend.dao.model;

import lombok.Data;
import net.txconsole.core.model.ProjectRole;

@Data
public class TProjectAuthorization {

    private final int project;
    private final int account;
    private final ProjectRole role;

}
