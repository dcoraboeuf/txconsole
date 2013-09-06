package net.txconsole.core;

public abstract class NotFoundException extends InputException {

    protected NotFoundException(Object... params) {
        super(params);
    }
}
