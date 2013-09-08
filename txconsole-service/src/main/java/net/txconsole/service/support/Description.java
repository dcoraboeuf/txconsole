package net.txconsole.service.support;

import com.google.common.base.Function;
import lombok.Data;
import net.sf.jstring.Strings;

import java.util.Locale;

@Data
public class Description {

    private final String id;
    private final String name;
    private final String description;

    public static Function<? super Descriptible, Description> fromDescriptible(final Strings strings, final Locale locale) {
        return new Function<Descriptible, Description>() {
            @Override
            public Description apply(Descriptible d) {
                return new Description(
                        d.getId(),
                        strings.get(locale, d.getNameKey()),
                        strings.get(locale, d.getDescriptionKey())
                );
            }
        };
    }
}
