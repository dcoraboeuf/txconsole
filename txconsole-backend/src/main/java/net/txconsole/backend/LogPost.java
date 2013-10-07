package net.txconsole.backend;

import net.txconsole.core.RunProfile;
import net.txconsole.core.model.Message;
import net.txconsole.core.model.MessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Profile({RunProfile.DEV, RunProfile.IT, RunProfile.TEST})
public class LogPost extends AbstractMessagePost {

    private final Logger logger = LoggerFactory.getLogger(LogPost.class);

    @Override
    public boolean supports(MessageChannel channel) {
        return true;
    }

    @Override
    public void post(Message message, Collection<String> destination) {
        logger.debug(
                "[message] Sending message to '{}':\n" +
                        "-----------------\n" +
                        "{}\n" +
                        "\n" +
                        "{}" +
                        "\n" +
                        "-----------------\n",
                destination,
                message.getTitle(),
                message.getContent());
    }

}
