package net.txconsole.backend;

import org.springframework.mail.javamail.JavaMailSender;

public interface MailService {

    JavaMailSender getMailSender();

}
