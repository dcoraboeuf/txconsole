extension.format.properties
    en -> Property files
    fr -> Fichiers de propriétés

extension.format.properties.description
    en -> Set of files using the JDK Properties format.
    fr -> Ensemble de fichiers au format Properties du JDK.

extension.format.properties.icu4j
    en -> ICU4J Property files
    fr -> Fichiers de propriétés ICU4J

extension.format.properties.icu4j.description
    en -> Set of files using the ICU4J Properties format.
    fr -> Ensemble de fichiers au format Properties ICU4J.

extension.format.properties.defaultLocale
    en -> Default locale
    fr -> Language par défaut

extension.format.properties.groups
    en -> List of groups
    fr -> Liste de groupes

extension.format.properties.groups.help
    en -> ...
        Property files are identified using the <group>_<locale>.properties format. The list
        of groups and their associated supported locales is defined by group=locale1,locale2...
        lines. Blank lines or lines started by # are ignored.
        ...
    fr -> ...
        Les fichiers de propriété sont identifiés grâce au format <group>_<locale>.properties. La liste
        des groupes et de leurs localisations associées est définie par des lignes au format
        group=locale1,locale2... Les lignes vides ou commençant par # sont ignorées.
        ...

[errors]

net.txconsole.extension.format.properties.PropertyFileNotFoundException
    en -> [PF-001] Property file "{0}" not found.
    fr -> [PF-001] Fichier de propriétés "{0}" non trouvé.

net.txconsole.extension.format.properties.PropertyFileCannotReadException
    en -> [PF-002] Property file "{0}" cannot be read.
    fr -> [PF-002] Fichier de propriétés "{0}" impossible à lire.

net.txconsole.extension.format.properties.PropertiesTxFileFormatIOException
    en -> [PF-003] Error while writing {0}.
    fr -> [PF-003] Erreur lors de l'écriture de {0}.
