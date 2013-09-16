package net.txconsole.backend.support;

import net.txconsole.service.support.IOContext;

import java.util.concurrent.atomic.AtomicReference;

public abstract class VersionedIOContext<I extends VersionedIOContext<I>> implements IOContext {

    private final AtomicReference<String> version = new AtomicReference<>();

    @Override
    public String getVersion() {
        return version.get();
    }

    @Override
    @SuppressWarnings("unchecked")
    public I withVersion(String version) {
        this.version.set(version);
        return (I) this;
    }
}
