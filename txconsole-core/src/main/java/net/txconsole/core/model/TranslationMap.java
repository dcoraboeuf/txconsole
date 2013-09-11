package net.txconsole.core.model;

import net.sf.jstring.model.BundleCollection;

/**
 * Defines an access to an annotated map of
 * translations, where keys are associated with
 * categories, groups and descriptions, and labels
 * are associated with different languages.
 */
public class TranslationMap {

    private final BundleCollection bundleCollection;

    public TranslationMap(BundleCollection bundleCollection) {
        this.bundleCollection = bundleCollection;
    }

    public BundleCollection getBundleCollection() {
        return bundleCollection;
    }
}
