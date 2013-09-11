package net.txconsole.core.support;

import java.util.Comparator;
import java.util.Locale;

public class LocaleComparator implements Comparator<Locale> {

    public static final LocaleComparator INSTANCE = new LocaleComparator();

    @Override
    public int compare(Locale o1, Locale o2) {
        // FIXME English should always be first
        return o1.toString().compareTo(o2.toString());
    }
}