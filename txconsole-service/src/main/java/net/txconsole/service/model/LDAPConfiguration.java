package net.txconsole.service.model;

import lombok.Data;
import net.txconsole.service.validation.LDAPConfigurationValidation;

@Data
public class LDAPConfiguration implements LDAPConfigurationValidation {

    private int sequence;
    private boolean enabled;
    private String host;
    private Integer port;
    private String searchBase;
    private String searchFilter;
    private String user;
    private String password;
    private String fullNameAttribute;
    private String emailAttribute;

}
