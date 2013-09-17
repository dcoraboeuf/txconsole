package net.txconsole.core.support;

public interface IOContextFactory {

    IOContext createContext(String category);

    IOContext getOrCreateContext(String category, String idInCategory);
}
