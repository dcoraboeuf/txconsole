package net.txconsole.extension.format.properties;

import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class PropertyGroup {

    private final String name;
    private final List<Locale> locales;

}
