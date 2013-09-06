package net.txconsole.core.validation;

import net.txconsole.core.Patterns;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public interface AccountValidation {

    @NotNull
    @Size(min = 1, max = 16)
    @Pattern(regexp = Patterns.ACCOUNT_NAME_PATTERN)
    String getName();

    @NotNull
    @Size(min = 0, max = 80)
    String getFullName();

    @NotNull
    @Size(min = 1, max = 80)
    String getEmail();

}
