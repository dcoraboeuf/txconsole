package net.txconsole.service.support;

import net.txconsole.core.model.TranslationMap;
import net.txconsole.core.model.VersionFormat;

import java.util.Locale;
import java.util.Set;

/**
 * Provides access to a {@link net.txconsole.core.model.TranslationMap},
 * in writing or in reading.
 */
public interface TranslationSource<C> extends Configurable<C> {

    /**
     * Reads the translation map using a given configuration and for a given version.
     *
     * @param config  The configuration to use
     * @param version The version of the translation map to get. The semantics
     *                of this version vary according to the nature of the translation source. It will
     *                typically be associated with a SCM identifier like a revision or a tag. Giving
     *                <code>null</code> for this <code>version</code> is a explicit request for the
     *                <i>latest</i> version of the translation map.
     */
    TranslationMap read(C config, String version);

    /**
     * This method returns the label and the validation pattern to use for the version semantics. For example,
     * a Subversion-based translation source would return "Revision" while a Git-based one would
     * return "Tag or commit".
     *
     * @param config Configuration to use
     */
    VersionFormat getVersionSemantics(C config);

    /**
     * Writes the translation map using a given configuration. The translation map
     * is written back into the <i>latest</i> version of the translation source.
     *
     * @param config The configuration to use
     * @param map    The translation map to write back
     */
    void write(C config, TranslationMap map);

    Locale getDefaultLocale(C configuration);

    Set<Locale> getSupportedLocales(C configuration);
}
