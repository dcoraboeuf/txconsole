package net.txconsole.core.security;

import java.util.Arrays;
import java.util.List;

public interface SecurityRoles {

    String ADMINISTRATOR = "ROLE_ADMIN";

    String USER = "ROLE_USER";

    List<String> ALL = Arrays.asList(ADMINISTRATOR, USER);

}
