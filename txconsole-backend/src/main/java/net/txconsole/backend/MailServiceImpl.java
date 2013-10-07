package net.txconsole.backend;


import net.txconsole.backend.config.Caches;
import net.txconsole.core.RunProfile;
import net.txconsole.service.AdminService;
import net.txconsole.service.model.MailConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
@Profile({RunProfile.DEV, RunProfile.PROD})
public class MailServiceImpl implements MailService {

    private final Logger logger = LoggerFactory.getLogger(MailService.class);
    private final AdminService adminService;

    @Autowired
    public MailServiceImpl(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * The mail session is created once and then cached. It will be re-initialised
     * only if the mail configuration is changed.
     *
     * @return <code>null</code> if the mail cannot be configured (host being null or blank)
     * @see AdminService#saveMailConfiguration(net.txconsole.service.model.MailConfiguration)
     */
    @Override
    @Cacheable(value = Caches.MAIL, key = "'0'")
    public JavaMailSender getMailSender() {
        MailConfiguration configuration = adminService.getMailConfiguration();
        String host = configuration.getHost();
        if (StringUtils.isBlank(host)) {
            logger.info("[mail] No host configured - mail is not available");
            return null;
        }
        logger.debug("[mail] Creating mail sender");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        logger.debug("[mail] Host={}", host);
        mailSender.setHost(host);
        Properties p = new Properties();
        p.put("mail.smtp.debug", "true");
        boolean authentication = configuration.isAuthentication();
        if (authentication) {
            logger.debug("[mail] Using authentication");
            mailSender.setUsername(configuration.getUser());
            mailSender.setPassword(configuration.getPassword());
            mailSender.setPort(587);
            p.put("mail.smtp.auth", "true");
            boolean startTls = configuration.isStartTls();
            if (startTls) {
                logger.debug("[mail] STARTTLS required");
                p.put("mail.smtp.starttls.enable", "true");
                p.put("mail.smtp.starttls.required", "true");
            }
        }
        mailSender.setJavaMailProperties(p);
        return mailSender;
    }

}
