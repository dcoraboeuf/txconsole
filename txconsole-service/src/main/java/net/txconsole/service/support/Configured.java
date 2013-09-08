package net.txconsole.service.support;

import lombok.Data;

@Data
public class Configured<C, T extends Configurable<C>> {

    private final C configuration;
    private final T configurable;

}
