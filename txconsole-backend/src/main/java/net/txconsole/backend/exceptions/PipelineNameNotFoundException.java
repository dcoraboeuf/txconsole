package net.txconsole.backend.exceptions;

import net.txconsole.core.NotFoundException;

public class PipelineNameNotFoundException extends NotFoundException {
    public PipelineNameNotFoundException(String name) {
        super(name);
    }
}
