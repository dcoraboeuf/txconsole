package net.txconsole.service.support;

public interface IOContextFactory {

    IOContext createContext(String category);

    IOContext getOrCreateContext(String category, String idInCategory);
}
