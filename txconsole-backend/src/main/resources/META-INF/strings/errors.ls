net.txconsole.backend.exceptions.AccountAlreadyExistException
    en -> [E-001] Account "{0}" already exists.
    fr -> [E-001] L'utilisateur "{0}" existe déjà.

net.txconsole.backend.exceptions.ConfigurationKeyMissingException
    en -> [E-002] Configuration key [{0}] is missing.
    fr -> [E-002] Clef de configuration manquante : {0}

net.txconsole.backend.exceptions.ProjectAlreadyExistException
    en -> [E-003] Project "{0}" already exists.
    fr -> [E-003] Le projet "{0}" existe déjà.

net.txconsole.backend.exceptions.ConfigIDException
    en -> [E-004] Service "{0}" with ID "{1}" does not exist.
    fr -> [E-004] Le service "{0}" d'ID "{1}" n'existe pas.

net.txconsole.backend.exceptions.ConfigIOException
    en -> [E-005] Cannot read/write configuration for the "{0}" service of ID "{1}".
    fr -> [E-005] Impossible de lire/écrire la configuration pour le service "{0}" d'ID "{1}".

net.txconsole.backend.exceptions.ProjectParametersNotDefinedByBranchException
    en -> [E-006] The branch must define values for the following project parameters: {0}
    fr -> [E-006] La branche doit définir des valeurs pour les paramètres de projet suivants : {0}

net.txconsole.backend.exceptions.ProjectParametersNotDefinedException
    en -> [E-007] The branch defines some parameters not defined at project level: {0}
    fr -> [E-007] La branche définit des paramètres qui ne sont pas définis au niveau du projet : {0}

net.txconsole.backend.exceptions.ProjectParameterNotDefinedException
    en -> [E-008] The parameter "{0}" has not been defined.
    fr -> [E-008] Le paramètre "{0}" n'est pas défini.

net.txconsole.backend.exceptions.RequestUploadIOException
    en -> [E-009] Cannot read file {0}.
    fr -> [E-009] Impossible de lire le fichier {0}.

net.txconsole.backend.exceptions.RequestNoRequestFileException
    en -> [E-010] Cannot find any request file for {0}.
    fr -> [E-010] Impossible de trouver un fichier pour la demande {0}.

net.txconsole.backend.exceptions.DeletedRequestEntryCannotBeEditedException
    en -> [E-011] A deleted key cannot be edited.
    fr -> [E-011] Une clef supprimée ne peut pas être éditée.

# E-012

net.txconsole.backend.exceptions.TranslationDiffEntryNotFoundException
    en -> [E-013] Request entry for {0}:{1}:{2} not found.
    fr -> [E-013] Entrée {0}:{1}:{2} non trouvée.

net.txconsole.backend.exceptions.RequestCannotUploadBecauseOfStatusException
    en -> [E-014] Cannot upload response file for a request with status = {0}. Status {1} was expected.
    fr -> [E-014] Impossible d'uploader un fichier de réponse pour une demande de statut {0}. Le statut {1} est attendu.

net.txconsole.backend.exceptions.RequestCannotMergeBecauseOfStatusException
    en -> [E-015] Cannot merge a request with status = {0}. Status {1} was expected.
    fr -> [E-015] Impossible de réintégrer une demande avec le statut {0}. Le statut {1} est attendu.

net.txconsole.backend.exceptions.RequestCannotBeEditedException
    en -> [E-016] Cannot edit the request because its state does not allow it.
    fr -> [E-016] Impossible d'éditer la demande parce que sont status courant ne l'autorise pas.

net.txconsole.backend.exceptions.TemplateMergeException
    en -> [E-017] Error while merging template {0}.

net.txconsole.backend.exceptions.TemplateNotFoundException
    en -> [E-018] Cannot find template with name {0}.
