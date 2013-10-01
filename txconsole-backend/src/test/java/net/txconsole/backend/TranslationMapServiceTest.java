package net.txconsole.backend;

import net.sf.jstring.builder.BundleBuilder;
import net.sf.jstring.builder.BundleCollectionBuilder;
import net.sf.jstring.builder.BundleKeyBuilder;
import net.sf.jstring.builder.BundleSectionBuilder;
import net.sf.jstring.model.Bundle;
import net.txconsole.core.model.*;
import net.txconsole.core.support.MapBuilder;
import net.txconsole.service.StructureService;
import net.txconsole.service.TranslationMapService;
import org.junit.Test;

import java.util.Arrays;
import java.util.Locale;

import static net.txconsole.test.Helper.sampleDiff;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TranslationMapServiceTest {

    @Test
    public void diff() {
        // Services
        StructureService structureService = mock(StructureService.class);
        TranslationMapService service = new TranslationMapServiceImpl(structureService);
        // Old map
        TranslationMap oldMap = new TranslationMap(
                "1",
                BundleCollectionBuilder.create()
                        .bundle(
                                BundleBuilder.create("common")
                                        .section(
                                                BundleSectionBuilder
                                                        .create(Bundle.DEFAULT_SECTION)
                                                        .key(
                                                                BundleKeyBuilder.create("updated.default-only")
                                                                        .addValue(Locale.ENGLISH, "Initial value")
                                                                        .addValue(Locale.FRENCH, "Valeur initiale")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("updated.both")
                                                                        .addValue(Locale.ENGLISH, "Initial value")
                                                                        .addValue(Locale.FRENCH, "Valeur initiale")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("deleted.default-only")
                                                                        .addValue(Locale.ENGLISH, "Initial value")
                                                                        .addValue(Locale.FRENCH, "Valeur initiale")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("deleted.both")
                                                                        .addValue(Locale.ENGLISH, "Initial value")
                                                                        .addValue(Locale.FRENCH, "Valeur initiale")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("unchanged")
                                                                        .addValue(Locale.ENGLISH, "Unchanged")
                                                                        .addValue(Locale.FRENCH, "Inchangé")
                                                        )
                                        )
                                        .build()
                        )
                        .build()
        );
        // New map
        TranslationMap newMap = new TranslationMap(
                "2",
                BundleCollectionBuilder.create()
                        .bundle(
                                BundleBuilder.create("common")
                                        .section(
                                                BundleSectionBuilder
                                                        .create(Bundle.DEFAULT_SECTION)
                                                        .key(
                                                                BundleKeyBuilder.create("added.default-only")
                                                                        .addValue(Locale.ENGLISH, "Added default only")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("added.both")
                                                                        .addValue(Locale.ENGLISH, "Added both")
                                                                        .addValue(Locale.FRENCH, "Ajout des deux")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("updated.default-only")
                                                                        .addValue(Locale.ENGLISH, "Updated default only")
                                                                        .addValue(Locale.FRENCH, "Valeur initiale")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("updated.both")
                                                                        .addValue(Locale.ENGLISH, "Updated both")
                                                                        .addValue(Locale.FRENCH, "Les deux sont modifiés")
                                                        )
                                                        .key(
                                                                BundleKeyBuilder.create("deleted.default-only")
                                                                        .addValue(Locale.FRENCH, "Valeur initiale")
                                                        )
                                                                // .key(
                                                                //        BundleKeyBuilder.create("deleted.both")
                                                                // )
                                                        .key(
                                                                BundleKeyBuilder.create("unchanged")
                                                                        .addValue(Locale.ENGLISH, "Unchanged")
                                                                        .addValue(Locale.FRENCH, "Inchangé")
                                                        )
                                        )
                                        .build()
                        )
                        .build()
        );
        // Computes the diff
        TranslationDiff diff = service.diff(Locale.ENGLISH, oldMap, newMap).sorted();
        // Checks
        assertEquals(sampleDiff(), diff);
    }

    @Test
    public void diff_to_edit() {
        TranslationDiff diff = sampleDiff();
        diff = diff.forEdition(Arrays.asList(Locale.ENGLISH, Locale.FRENCH));
        assertEquals(
                Arrays.asList(
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "added.default-only",
                                TranslationDiffType.ADDED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, null, "Added default only"),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, null, null)
                                )
                        ),
                        new TranslationDiffEntry(
                                0,
                                "common",
                                "default",
                                "updated.default-only",
                                TranslationDiffType.UPDATED,
                                MapBuilder.dual(
                                        Locale.ENGLISH, new TranslationDiffEntryValue(0, Locale.ENGLISH, "Initial value", "Updated default only"),
                                        Locale.FRENCH, new TranslationDiffEntryValue(0, Locale.FRENCH, "Valeur initiale", "Valeur initiale")
                                )
                        )
                ),
                diff.getEntries()
        );
    }

}
