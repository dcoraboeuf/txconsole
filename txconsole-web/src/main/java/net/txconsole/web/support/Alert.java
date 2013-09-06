package net.txconsole.web.support;

import lombok.Data;
import net.txconsole.core.UserMessageType;

@Data
public class Alert {

    private final UserMessageType type;
    private final String message;

}
