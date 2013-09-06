package net.txconsole.core.model;

import lombok.Data;
import net.txconsole.core.validation.AccountValidation;

@Data
public class AccountCreationForm implements AccountValidation {

    private final String name;
    private final String fullName;
    private final String email;
    private final String roleName;
    private final String mode;
    private final String password;
    private final String passwordConfirm;

}
