package net.txconsole.service.support;

/**
 * Defines a way to access some files.
 *
 * @param <C> Type of the configuration
 */
public interface TxFileSource<C> extends Configurable<C> {

    /**
     * Method to get access to the files
     */
    FileSource getSource();

    // TODO Method to check the sync
    // TODO Method to sync down
    // TODO Method to sync up

}
