package net.txconsole.service.model;

public enum ConfigurationKey {
    /**
     * General
     */
    GENERAL_BASE_URL,
    /**
     * LDAP
     */
    LDAP_HOST, LDAP_PORT, LDAP_SEARCH_BASE, LDAP_SEARCH_FILTER, LDAP_USER, LDAP_PASSWORD, LDAP_ENABLED, LDAP_FULLNAME_ATTRIBUTE, LDAP_EMAIL_ATTRIBUTE,
    /**
     * Mail
     */
    MAIL_HOST, MAIL_AUTHENTICATION, MAIL_START_TLS, MAIL_USER, MAIL_REPLY_TO_ADDRESS, MAIL_PASSWORD
}
