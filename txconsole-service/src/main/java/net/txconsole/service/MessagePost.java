package net.txconsole.service;

import net.txconsole.core.model.Message;
import net.txconsole.core.model.MessageChannel;

import java.util.Collection;

public interface MessagePost {

    boolean supports(MessageChannel channel);

    void post(Message message, Collection<String> destination);

}
