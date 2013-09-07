package net.txconsole.service.support;

public class AbstractConfigurable<C> extends AbstractDescriptible implements Configurable<C> {

    private final Class<C> configClass;
    private C config;

    public AbstractConfigurable(String id, String nameKey, String descriptionKey, Class<C> configClass) {
        super(id, nameKey, descriptionKey);
        this.configClass = configClass;
    }

    @Override
    public Class<C> getConfigClass() {
        return configClass;
    }

    @Override
    public C getConfig() {
        return config;
    }

    @Override
    public void setConfig(C config) {
        this.config = config;
    }
}
