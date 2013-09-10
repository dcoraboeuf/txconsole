package net.txconsole.service.support;

/**
 * Defines a way to access some files.
 *
 * @param <C> Type of the configuration
 */
public interface TxFileSource<C> extends Configurable<C> {

    /**
     * Method to get access to the files for this source, using the specific version.
     *
     * @param config  The configuration to use
     * @param version The version of the file source to get. The semantics
     *                of this version vary according to the nature of the file source. It will
     *                typically be associated with a SCM identifier like a revision or a tag. Giving
     *                <code>null</code> for this <code>version</code> is a explicit request for the
     *                <i>latest</i> version of the file source.
     * @see TranslationSource#read(Object, String)
     */
    FileSource getSource(C config, String version);

    /**
     * This method returns the label and the validation pattern to use for the version semantics. For example,
     * a Subversion-based file source would return "Revision" while a Git-based one would
     * return "Tag or commit".
     */
    VersionFormat getVersionSemantics();

    // TODO Method to check the sync
    // TODO Method to sync down
    // TODO Method to sync up

}
