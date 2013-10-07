package net.txconsole.core.model;

import lombok.Data;

@Data
public class Contribution {

    public static Contribution empty() {
        return new Contribution();
    }

}
