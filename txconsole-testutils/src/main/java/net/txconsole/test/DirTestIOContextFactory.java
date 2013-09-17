package net.txconsole.test;

import net.txconsole.core.support.DirIOContextFactory;

import java.io.File;

public class DirTestIOContextFactory extends DirIOContextFactory {

    private final File root = new File("target/work");

    @Override
    protected File getRootDir() {
        return root;
    }
}
