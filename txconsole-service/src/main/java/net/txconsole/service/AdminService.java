package net.txconsole.service;

import net.txconsole.service.model.GeneralConfiguration;
import net.txconsole.service.model.LDAPConfiguration;
import net.txconsole.service.model.MailConfiguration;

public interface AdminService {

    GeneralConfiguration getGeneralConfiguration();

    void saveGeneralConfiguration(GeneralConfiguration configuration);

    LDAPConfiguration getLDAPConfiguration();

    void saveLDAPConfiguration(LDAPConfiguration configuration);

    MailConfiguration getMailConfiguration();

    void saveMailConfiguration(MailConfiguration configuration);
}
