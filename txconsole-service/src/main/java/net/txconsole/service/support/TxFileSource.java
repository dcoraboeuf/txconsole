package net.txconsole.service.support;

import com.google.common.base.Function;
import net.txconsole.core.model.VersionFormat;
import net.txconsole.core.support.IOContext;

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
    IOContext getSource(C config, String version);

    /**
     * This method returns the label and the validation pattern to use for the version semantics. For example,
     * a Subversion-based file source would return "Revision" while a Git-based one would
     * return "Tag or commit".
     */
    VersionFormat getVersionSemantics();

    /**
     * Performs an action in a given context and synchronizes the updated source with the initial source.
     *
     * @param config  The configuration to use
     * @param version The version of the file source to get. See {@link #getSource(Object, String)} for details.
     * @param message The message associated with the action
     * @param action  The action to execute in this context
     * @param <T>     The type of value returned by the action
     * @return The value returned by the action, associated with the new version of the source
     */
    <T> TxFileSourceResult<T> withSource(C config, String version, String message, Function<IOContext, T> action);

}
