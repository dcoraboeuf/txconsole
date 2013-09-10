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

project.languages.help
    en -> List of languages, separated by commas.
    fr -> Liste des langages, séparés par des virgules.

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

request.create
    en -> Create a translation request
    fr -> Créer une demande de traductions
