package net.txconsole.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.txconsole.core.support.TimeUtils;
import org.joda.time.DateTime;

@Data
@AllArgsConstructor
public class Signature {

    private final Integer authorId;
    private final String authorName;
    private final DateTime timestamp;

    public Signature(int id, String name) {
        this(id, name, TimeUtils.now());
    }

    public static Signature anonymous() {
        return new Signature(
                0,
                ""
        );
    }

}
