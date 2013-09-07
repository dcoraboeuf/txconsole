package net.txconsole.service.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public abstract class AbstractDescriptible implements Descriptible {

    @Getter
    private final String id;
    @Getter
    private final String nameKey;
    @Getter
    private final String descriptionKey;

}
