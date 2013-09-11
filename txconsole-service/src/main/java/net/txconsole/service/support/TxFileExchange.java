package net.txconsole.service.support;

/**
 * Defines the way to exchange translation files with the outside world. This interface
 * defines a way to 1) create the output file 2) read the returned file
 */
public interface TxFileExchange<C> extends Configurable<C> {

    // TODO Method to generate some files from a translation map
    // TODO Method to read some files into a translation map

}
