package net.txconsole.backend.exceptions;

import net.txconsole.core.InputException;

public class AccountAlreadyExistException extends InputException {
    public AccountAlreadyExistException(String name) {
        super(name);
    }
}
