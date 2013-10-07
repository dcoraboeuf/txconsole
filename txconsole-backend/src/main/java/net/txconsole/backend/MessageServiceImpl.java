package net.txconsole.backend;

import net.txconsole.core.model.Message;
import net.txconsole.core.model.MessageChannel;
import net.txconsole.core.model.MessageDestination;
import net.txconsole.service.MessagePost;
import net.txconsole.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Collection;

@Service
public class MessageServiceImpl implements MessageService {

    private final ApplicationContext applicationContext;
    private Collection<MessagePost> posts;

    @Autowired
    public MessageServiceImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        posts = applicationContext.getBeansOfType(MessagePost.class).values();
    }

    @Override
    public void sendMessage(Message message, MessageDestination messageDestination) {
        MessageChannel channel = messageDestination.getChannel();
        Collection<String> destination = messageDestination.getDestination();
        for (MessagePost post : posts) {
            if (post.supports(channel)) {
                post.post(message, destination);
            }
        }
    }

}
