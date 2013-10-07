[language]

language.en
	en,fr -> English
language.fr
	en,fr -> Français

[general]

general.cancel
	en -> Cancel
	fr -> Annuler

general.submit
	en -> Submit
	fr -> Envoyer

general.close
	en -> Close
	fr -> Fermer

general.create
	en -> Create
	fr -> Créer

general.modify
	en -> Modify
	fr -> Modifier

general.delete
	en -> Delete
	fr -> Supprimer

general.confirm.title
	en,fr -> Confirmation

general.loading
	en -> Loading...
	fr -> Chargement en cours...

general.empty
    en -> No entry
    fr -> Aucune entrée

general.more
    en -> More...
    fr -> Plus...

general.error.technical
	en -> Technical error
	fr -> Erreur technique

general.error.contact
	en -> Please report the following message and identifier to the TxConsole administrator.
	fr -> Veuillez signaler le message et l'identifiant qui suivent à l'administrateur de TxConsole.

general.error.full
	en -> ...
		An error has occurred.\n
		@[general.error.contact]\n
		\n
		{0}\n
		Identifier: {1}
		...
	fr -> ...
		Une erreur est survenue.\n
		@[general.error.contact]\n
		{0}\n
		Identifiant : {1}
		...

[error]

error
	en -> Error
	fr -> Erreur

error.message
	en -> ...
		An error has occurred. We are sorry for any inconvenience.
		...
	fr -> ...
		Une erreur s'est produite. Nous nous excusons pour le problème rencontré.
		...

error.back
	en -> Go back to the portal
	fr -> Revenir à l'acceuil

net.txconsole.web.support.UploadTooBigException
    en -> File too big. Maximum is {0}K.
    fr -> Fichier trop gros. Le maximum est de {0} K.

[login]

login
    en -> Sign in
    fr -> Se connecter

login.user
    en -> User
    fr -> Utilisateur

login.password
    en -> Password
    fr -> Mot de passe

logout
    en -> Sign out
    fr -> Se déconnecter


[settings]

settings
    en -> Settings
    fr -> Configuration

settings.security
    en -> Security settings
    fr -> Configuration de la sécurité

settings.mail
    en -> Mail settings
    fr -> Configuration du courriel

settings.general
    en -> General configuration
    fr -> Configuration générale

[settings.general]

settings.general.baseUrl
    en -> Base URL
    fr -> URL de référence

settings.general.saved
    en -> General configuration has been saved.
    fr -> La configuration générale a été sauvegardée.

[settings.security.ldap]

ldap.enabled
    en -> Enable LDAP authentication
    fr -> Activer l'authentification par LDAP

ldap.host
    en -> LDAP server
    fr -> Serveur LDAP

ldap.port
    en -> LDAP server port
    fr -> Port du serveur LDAP

ldap.search.base
    en -> LDAP search base
    fr -> Base de recherche LDAP

ldap.search.filter
    en -> LDAP filter
    fr -> Filtre LDAP

ldap.user
    en -> LDAP user
    fr -> Utilisateur LDAP

ldap.password
    en -> LDAP password
    fr -> Mot de passe LDAP

ldap.fullNameAttribute
    en -> Full name attribute
    fr -> Attribut pour le nom complet

ldap.emailAttribute
    en -> Email attribute
    fr -> Attribut pour le courriel

ldap.saved
    en -> LDAP configuration has been saved.
    fr -> La configuration LDAP a été sauvegardée.

[settings.mail]

mail.saved
    en -> Mail configuration has been saved.
    fr -> La configuration du courriel a été sauvegardée.


mail.host
    en -> Mail server
    fr -> Serveur de courriel

mail.user
    en -> User
    fr -> Utilisateur

mail.password
    en -> Password
    fr -> Mot de passe

mail.authentication
    en -> Authentication
    fr -> Authentification

mail.startTls
    en -> Start TLS
    fr -> Démarrer TLS

mail.replyToAddress
    en -> Reply to address
    fr -> Adresse de retour

[accounts]

accounts
    en -> Accounts
    fr -> Utilisateurs

accounts.ldap-warning
    en -> The LDAP is not enabled and some users may not be able to connect.
    fr -> La configuration LDAP n'est pas activée et quelques utilisateurs pourraient ne pas pouvoir se connecter.

account.new
    en -> Create a new account
    fr -> Créer un nouvel utilisateur

account.name
    en -> Name
    fr -> Nom

account.fullName
    en -> Full name
    fr -> Nom complet

account.email
    en -> eMail
    fr -> Courriel

account.role
    en -> Role
    fr -> Rôle

account.role.ROLE_ADMIN
    en -> Administrator
    fr -> Administrateur
account.role.ROLE_USER
    en -> User
    fr -> Utilisateur
account.role.ROLE_ADMIN.help
    en -> Administrators can: manage other accounts, manage all entities (projects, branches...).
    fr -> Les administrateurs peuvent gérer les autres comptes, gérer toutes les entités (projets, branches, ...).
account.role.ROLE_USER.help
    en -> Users can perform actions that have been granted to them by administrators.
    fr -> Les utilisateurs ne peuvent effectuer que les actions qui leur ont été permises par des administrateurs.

account.mode
    en -> Authentication mode
    fr -> Mode d'authentification

account.mode.builtin
    en -> Built in
    fr -> Prédéfini

account.mode.ldap
    en,fr -> LDAP

account.locale
    en -> Language used for reports
    fr -> Langage utilisé pour les rapports

account.password
    en -> Password
    fr -> Mot de passe

account.password.confirm
    en -> Confirm password
    fr -> Confirmation du mot de passe

account.password.confirm.incorrect
    en -> Password confirmation is incorrect
    fr -> La confirmation du mot de passe est incorrecte.

account.delete
    en -> Account deletion
    fr -> Suppression d'un compte utilisateur

account.delete.message
    en -> Do you really want to delete the following account?
    fr -> Voulez-vous vraiment supprimer le compte suivant ?

account.deleted
    en -> Account has been deleted.
    fr -> L'utilisateur a été supprimé.

account.update
    en -> Account update
    fr -> Mise à jour d'un compte utilisateur

account.updated
    en -> Account has been updated.
    fr -> L'utilisateur a été mis à jour.

account.passwordReset
    en -> Reset password
    fr -> Réinitialiser le mot de passe

account.passwordReset.title
    en -> Reset password for {0}
    fr -> Réinitialiser du mot de passe de {0}

[profile]

profile
    en -> Profile
    fr -> Profil

profile.changeLanguage
    en -> Change language for reports
    fr -> Changer de langage pour les rapports

profile.changePassword
    en -> Change password
    fr -> Changer de mot de passe

profile.changePassword.ok
    en -> Password has been changed.
    fr -> Votre mot de passe a été changé.

profile.changePassword.nok
    en -> Password has not been changed. The old password may have been incorrect.
    fr -> Votre mot de passe n'a pas été changé. L'ancien mot de passe était peut-être incorrect.

profile.changeEmail
    en -> Change email
    fr -> Changer de courriel

profile.changeEmail.ok
    en -> ...
        Email has been changed. The change will be effective after
        you have sign out and in again.
        ...
    fr -> ...
        Votre courriel a été changé. Le changement sera effectif
        après votre reconnexion.
        ...

profile.changeEmail.nok
    en -> Email has not been changed. The password may have been incorrect.
    fr -> Votre courriel n'a pas été changé. Votre mot de passe était peut-être incorrect.

[password]

password
    en -> Password change
    fr -> Changement de mot de passe

password.user
    en -> User
    fr -> Utilisateur

password.oldPassword
    en -> Old password
    fr -> Ancien mot de passe

password.newPassword
    en -> New password
    fr -> Nouveau mot de passe

password.newPassword.confirm
    en -> Confirmation
    fr -> Confirmation

password.submit
    en -> Change
    fr -> Changer

password.confirmationNok
    en -> The password confirmation is not correct.
    fr -> La confirmation du mot de passe est incorrecte.

[email]

email.change
    en -> Email change
    fr -> Changement de courriel

email.change.user
    en -> User
    fr -> Utilisateur

email.change.password
    en -> Checking the password
    fr -> Vérification du mot de passe

email.change.email
    en -> New email
    fr -> Nouveau courriel

email.change.submit
    en -> Change
    fr -> Changer

[home]

home
    en -> Home
    fr -> Accueil

[project]

project.list
    en -> Projects
    fr -> Projets

project.create
    en -> New project
    fr -> Nouveau projet

project.delete.prompt
    en -> Are you sure to delete the project "{0}"?
    fr -> Etes-vous sûr de vouloir supprimer le projet "{0}" ?

[branch]

branch.list
    en -> Branches
    fr -> Branches

branch.create
    en -> New branch
    fr -> Nouvelle branche

[request]

request.list
    en -> Existing requests
    fr -> Demandes existantes

request.create
    en -> Create a translation request
    fr -> Créer une demande de traductions

request.create.version.help
    en -> ...
        Define the version to start from when computing the differences
        of the translations between this version and the most recent
        state. This version is normally computed from all past requests
        but can be blank if no previous request exists.
        ...
    fr -> ...
        Définit la version de départ pour calculer la liste des traductions
        entre cette version et la plus récente. Cette version est normalement
        calculée en fonction de la liste de toutes les demandes passées mais
        peut être vide quand aucune demande n'a encore été faite.
        ...

request.create.keys
    en -> Additional keys
    fr -> Clefs supplémentaires

request.create.keys.filter
    en -> Key filter
    fr -> Filtre pour les clefs

request.create.keys.filter.help
    en -> Enter some text that will be used to filter on both keys and labels.
    fr -> Saisissez du texte qui sera utilisé pour filter à la fois sur les clefs et les libellés.

request.request.download
    en -> Download request file
    fr -> Télécharger le fichier de demande

request.delete
    en -> Delete request
    fr -> Supprimer la demande

request.delete.prompt
    en -> Do you really want to delete this request?
    fr -> Voulez-vous vraiment supprimer cette demande ?

request.upload
    en -> Upload response
    fr -> Uploader réponse

request.upload.file
    en -> File
    fr -> Fichier

request.upload.file.add
    en -> Add a file
    fr -> Ajouter un fichier

request.merge
    en -> Merge
    fr -> Réintégrer

request.merge.message
    en -> Message associated with the merge
    fr -> Message associé à la réintégration

request.merge.force
    en -> ...
        There remains unfilled entries in the request. You can still force the merge to take place but
        this may lead to some keys not correctly filled in. Do you want to go on with the merge?
        ...
    fr -> ...
        Il y a encore des clefs qui ne sont pas correctement renseignées. Vous pouvez toujours forcer
        la réintégration mais cela pourrait conduire à une situation où des clefs ne sont pas
        correctement renseignées. Voulez-vous continuer avec la réintégration ?
        ...

request.hide.deleted
    en -> Hide deleted entries
    fr -> Cacher les entrées supprimées

request.hide.valid
    en -> Hide valid entries
    fr -> Cacher les entrées valides

[keyfilter]

keyfilter.go
    en -> Search
    fr -> Chercher

[acl]

acl.project
    en -> Authorizations
    fr -> Autorisations

acl.project.description
    en -> Management of authorizations for {0}.
    fr -> Gestion des autorisations pour {0}.

acl.account
    en -> Account
    fr -> Compte utilisateur

acl.project.role
    en -> Project role
    fr -> Rôle pour le projet

acl.project.add
    en -> Add
    fr -> Ajouter

[contributions]

contribution
    en -> Contribution
    fr -> Contribution

contribution.search
    en -> Search for keys/labels to edit...
    fr -> Cherchez les clefs ou libellés à éditer...

contribution.edit
    en -> Edit the labels...
    fr -> Editez les libellés...

contribution.edit.empty
    en -> Search for keys or labels in order to start edition.
    fr -> Cherchez des clefs ou des libellés afin de commencer l'édition.

contribution.edit.noresult
    en -> No result was found.
    fr -> Aucun résultat n'a été trouvé.

contribution.edit.total
    en -> Total:
    fr -> Total :

contribution.edit.locale.filter
    en -> Hide this locale
    fr -> Masquer ce language

contribution.manage
    en -> ... and commit them.
    fr -> ... et soumettez les.

contribution.manage.empty
    en -> Nothing to commit yet.
    fr -> Rien à soumettre.

