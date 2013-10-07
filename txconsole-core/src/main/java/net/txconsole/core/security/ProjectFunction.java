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
     * Editing the content of a request
     */
    REQUEST_EDIT,
    /**
     * Uploading the content of a request
     */
    REQUEST_UPLOAD,
    /**
     * merging a request back into the source
     */
    REQUEST_MERGE,
    /**
     * Deleting an existing translation request
     */
    REQUEST_DELETE,
    /**
     * Management of authorizations for the project
     */
    ACL,
    /**
     * Contribution is allowed
     */
    CONTRIBUTION,
    /**
     * Contribution is direct
     */
    CONTRIBUTION_DIRECT,
    /**
     * Review of contributions
     */
    CONTRIBUTION_REVIEW;

    /**
     * Security functions for {@link SecurityCategory#PROJECT}.
     */
    @Override
    public SecurityCategory getCategory() {
        return SecurityCategory.PROJECT;
    }
}
