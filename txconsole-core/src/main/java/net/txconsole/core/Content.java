package net.txconsole.core;

import lombok.Data;

@Data
public class Content {

    private final String type;
    private final byte[] bytes;

}
