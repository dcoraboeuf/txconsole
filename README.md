translation-console
===================

## Overview

The idea is to have a web application that allows the registered users to access some property files (or any other format) and to deal with the updates.

## Requirements

One project is defined with a translation source, that basically defines how to get a map of translations (key → list of labels per language).
Branches (or releases) can be defined at project level - they just provide another way to get the translations. A key can be associated with a description.

One authorized user can create translation requests. The input of such a request is built from:

 * the list of remaining translations ("holes" in the translation map)
 * any additional key

The output of a translation request is:

 * a translation map with associated meta-information per key
 * an output format (file representation - extensible)

Additionally, the tool should provide those functionalities:

 * detection of problems using heuristics
 * edition of labels after filtering on keys and/or labels

### Annotated translation maps

The translation sources are responsible to read and write translation maps which basically associate keys with labels for a list of languages.
Languages are defined using the Java Locale instances.

Keys are associated with a description (if made available by the file format). They are grouped in:
 * categories (optional)
 * groups (optional)

For example, using [jstring](https://github.com/dcoraboeuf/jstring) as a file format, the category would be the bundle,
and the group would be the section.

### Translation sources

A translation source is typically the association a file provider and a file format:

 * the file provider gives access to the files (Subversion, Git, folder...)
 * the file format is the protocol needed for accessing the files and transform them back and forth into translation maps:
  - jstring implementation
  - property files
  - …

The translation source is also responsible for re-integrating a translation map in the actual files.

### Translation request outputs

A translation request output is responsible to transform an annotated translation map into a file (or set of files) suitable for an exchange with the actual translators:

 * set of property files per languages
 * excel files
 * …

On the other hand, it will also be responsible for reading this format back when re-integrating the translations.

### Contributions

A user can select a project and a branch, filter on keys or labels, and get a list of keys and labels per language. According to its level of authorization, he will be able to

 * edit the labels
 * add new keys
 * delete keys

If the user is a Reviewer (see authorizations below), his changes will be directly written back to the source. If he is only a Contributor, his changes will be sent for review before being actually sent. All the reviewers of a project will be notified when such a contribution is created.

### Authorizations

At application level, registered users can be Main user roles are:

 * administrators
     * they can create and delete projects
     * they can define the settings for the application
         * list of available extensions
         * global settings (security, mail…)
     * they can add users
     * they assign Owners to projects
 * users - they cannot do anything at application level


At project level, several authorizations can be defined:

 * owners
      * they can assign other users to this project
      * they can manage the settings for this project
          * translation source
          * translation request output
      * they can manage the branches for this project
          * additional settings for the translation source
      * they can create translation requests
      * they can merge the translation requests once edited by the translators
 * translators
      * they can edit the translation requests
 * reviewers
      * they can edit the translations and write them back without review
      * they can review the contributions, edit them and write them back (or cancel them)
 * contributors
      * they can create contributions

## Implementation notes

### Storage

Database or files? Dealing with extensions with a database may prove difficult, but on the other hand, dealing with transactions with
files may prove difficult. Using a local JSON database or JSON columns would be a solution.

### Extensibility

Most of the features should work through extensions: translation sources, translation request outputs, file sources, file formats…
Each extension must come with its code of course, but also with everything it needs for its configuration. For example, when one user
selects “Git” for file source, he should be the corresponding configuration form appearing and this page fragment should be generated
by the extension, not by using any kind of generic parameter framework. Using requirejs to load the corresponding extension code
should be enough to deal with this kind of architecture.

### Extensibility through scripting

An easy way to allow for extensibility is to allow configurators (administrators or owners) to enter some basic scripts to perform some actions (like typing some
Groovy in order to detect problems).
The execution of such scripts must be sandboxed using mechanisms like [Java-Sandbox](http://blog.datenwerke.net/2013/06/sandboxing-groovy-with-java-sandbox.html) or the Groovy Shell (from the Groovy library).

### Working copies

Most of file sources will require a working copy in order to have access to the raw files: Subversion, Git...

The file source must be able to access the working copy in a controlled way:

 * no need to synchronize (or clone) each time
 * ... but synchronized access in order to allow conflicts

This can be performed by:

 * having a scheduled service updating (or creating, or cleaning) the working copies
* having a service that ensures a synchronous access to those working copies

### API

All features of the application must be accessible through a HTTP API. The GUI must always refer to this API in order to work.
