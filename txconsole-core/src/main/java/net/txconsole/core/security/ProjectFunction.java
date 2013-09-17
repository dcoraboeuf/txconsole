package net.txconsole.core.security;

/**
 * List of functions for a project.
 */
public enum ProjectFunction implements SecurityFunction {

    /**
     * Updating the configuration of the project
     */
    UPDATE,

    /**
     * Deleting the project
     */
    DELETE,

    /**
     * Creating a translation request for the project
     */
    REQUEST_CREATE,
    /**
     * Deleting an existing translation request
     */
    REQUEST_DELETE;

    /**
     * Security functions for {@link SecurityCategory#PROJECT}.
     */
    @Override
    public SecurityCategory getCategory() {
        return SecurityCategory.PROJECT;
    }
}
