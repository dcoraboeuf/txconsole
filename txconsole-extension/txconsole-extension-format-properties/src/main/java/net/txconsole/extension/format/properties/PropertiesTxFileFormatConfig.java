package net.txconsole.extension.format.properties;

import lombok.Data;

import java.util.List;
import java.util.Locale;

@Data
public class PropertiesTxFileFormatConfig {

    private final Locale defaultLocale;
    private final List<PropertyGroup> groups;

}
