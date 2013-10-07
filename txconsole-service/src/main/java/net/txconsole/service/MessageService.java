package net.txconsole.service;

import net.txconsole.core.model.Message;
import net.txconsole.core.model.MessageDestination;

public interface MessageService {

    void sendMessage(Message message, MessageDestination messageDestination);

}
