package net.txconsole.extension.format.properties;

import lombok.Data;

import java.util.List;

@Data
public class PropertiesTxFileFormatConfig {

    private final List<PropertyGroup> groups;

}
