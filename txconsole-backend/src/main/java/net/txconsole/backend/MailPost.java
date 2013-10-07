package net.txconsole.backend;

import net.txconsole.core.RunProfile;
import net.txconsole.core.model.Message;
import net.txconsole.core.model.MessageChannel;
import net.txconsole.core.model.MessageContentType;
import net.txconsole.service.AdminService;
import net.txconsole.service.model.MailConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Collection;

@Component
@Profile({RunProfile.DEV, RunProfile.PROD})
public class MailPost extends AbstractMessagePost {

    private final Logger logger = LoggerFactory.getLogger(MailPost.class);
    private final MailService mailService;
    private final AdminService adminService;

    @Autowired
    public MailPost(MailService mailService, AdminService adminService) {
        this.mailService = mailService;
        this.adminService = adminService;
    }

    @Override
    public boolean supports(MessageChannel channel) {
        return (channel == MessageChannel.EMAIL);
    }

    @Override
    public void post(final Message message, final Collection<String> destination) {

        // Mail sender
        JavaMailSender mailSender = mailService.getMailSender();
        if (mailSender == null) {
            // Mail not configured - not sending anything
            return;
        }

        MailConfiguration configuration = adminService.getMailConfiguration();

        // Reply to address
        final String replyToAddress = configuration.getReplyToAddress();
        logger.debug("[mail] Sending message from: {}", replyToAddress);

        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                prepareMessage(mimeMessage, message, destination, replyToAddress);
            }
        };

        try {
            mailSender.send(preparator);
            logger.debug("[mail] Message sent from: {}", replyToAddress);
        } catch (MailException ex) {
            logger.error("[mail] Cannot send mail: {}", ExceptionUtils.getRootCauseMessage(ex));
        }
    }

    protected void prepareMessage(MimeMessage mimeMessage, Message message, Collection<String> destination, String replyToAddress) throws MessagingException {
        mimeMessage.setRecipients(MimeMessage.RecipientType.TO, StringUtils.join(destination, ","));
        mimeMessage.setFrom(new InternetAddress(replyToAddress));
        mimeMessage.setSubject(message.getTitle());
        MessageContentType messageContentType = message.getContent().getType();
        switch (messageContentType) {
            case PLAIN:
                mimeMessage.setText(message.getContent().getText());
                break;
            case HTML:
                mimeMessage.setContent(message.getContent().getText(), "text/html");
                break;
            default:
                throw new IllegalStateException("Unknown message content type: " + messageContentType);
        }
    }

}
