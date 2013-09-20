package net.txconsole.core;

import lombok.Data;

@Data
public class NamedContent {

    private final String name;
    private final String type;
    private final byte[] bytes;
}
