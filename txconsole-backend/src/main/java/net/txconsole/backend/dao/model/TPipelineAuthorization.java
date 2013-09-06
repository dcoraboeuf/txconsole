package net.txconsole.backend.dao.model;

import lombok.Data;
import net.txconsole.core.model.PipelineRole;

@Data
public class TPipelineAuthorization {

    private final int pipeline;
    private final int account;
    private final PipelineRole role;

}
