package net.txconsole.service.support;

import com.google.common.base.Function;

/**
 * Defines an object that can be described.
 */
public interface Descriptible {

    Function<? super Descriptible, String> idFn = new Function<Descriptible, String>() {
        @Override
        public String apply(Descriptible d) {
            return d.getId();
        }
    };

    /**
     * Returns a unique ID for this object type
     */
    String getId();

    /**
     * Returns its name as a localization key
     */
    String getNameKey();

    /**
     * Returns its description as a localization key
     */
    String getDescriptionKey();

}
