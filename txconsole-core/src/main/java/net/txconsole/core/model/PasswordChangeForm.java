package net.txconsole.core.model;

import lombok.Data;

@Data
public class PasswordChangeForm {

    private String oldPassword;
    private String newPassword;

}
