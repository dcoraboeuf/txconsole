package net.txconsole.service.model;

import lombok.Data;

@Data
public class MailConfiguration {

    private String host;
    private String user;
    private String password;
    private boolean authentication;
    private boolean startTls;
    private String replyToAddress;

}
