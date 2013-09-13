package net.txconsole.backend.dao.impl;

public interface SQL {

    // Project
    String PROJECT_ALL = "SELECT * FROM PROJECT ORDER BY NAME";
    String PROJECT_BY_ID = "SELECT * FROM PROJECT WHERE ID = :id";
    String PROJECT_CREATE = "INSERT INTO PROJECT (NAME, FULLNAME, TXSOURCE_ID, TXSOURCE_CONFIG) VALUES (:name, :fullName, :txsource_id, :txsource_config)";
    String PROJECT_DELETE = "DELETE FROM PROJECT WHERE ID = id";
    // Configuration
    String CONFIGURATION_GET = "SELECT VALUE FROM CONFIGURATION WHERE NAME = :name";
    String CONFIGURATION_DELETE = "DELETE FROM CONFIGURATION WHERE NAME = :name";
    String CONFIGURATION_INSERT = "INSERT INTO CONFIGURATION (NAME, VALUE) VALUES (:name, :value)";
    // Accounts
    String ACCOUNT_AUTHENTICATE = "SELECT ID, NAME, FULLNAME, EMAIL, ROLENAME, MODE, LOCALE FROM ACCOUNT WHERE MODE = 'builtin' AND NAME = :user AND PASSWORD = :password";
    String ACCOUNT_ROLE = "SELECT ROLENAME FROM ACCOUNT WHERE MODE = :mode AND NAME = :user";
    String ACCOUNT_BY_NAME = "SELECT ID, NAME, FULLNAME, EMAIL, ROLENAME, MODE, LOCALE FROM ACCOUNT WHERE MODE = :mode AND NAME = :user";
    String ACCOUNT = "SELECT * FROM ACCOUNT WHERE ID = :id";
    String ACCOUNT_LIST = "SELECT ID, NAME, FULLNAME, EMAIL, ROLENAME, MODE, LOCALE FROM ACCOUNT ORDER BY NAME";
    String ACCOUNT_USER_LIST = "SELECT ID, NAME, FULLNAME, EMAIL, ROLENAME, MODE, LOCALE FROM ACCOUNT WHERE ROLENAME = 'ROLE_USER' ORDER BY NAME";
    String ACCOUNT_CREATE = "INSERT INTO ACCOUNT (NAME, FULLNAME, EMAIL, ROLENAME, MODE, PASSWORD) VALUES (:name, :fullName, :email, :roleName, :mode, :password)";
    String ACCOUNT_DELETE = "DELETE FROM ACCOUNT WHERE ID = :id";
    String ACCOUNT_UPDATE = "UPDATE ACCOUNT SET NAME = :name, FULLNAME = :fullName, EMAIL = :email, ROLENAME = :roleName WHERE ID = :id";
    String ACCOUNT_CHANGE_PASSWORD = "UPDATE ACCOUNT SET PASSWORD = :newPassword WHERE ID = :id AND MODE = 'builtin' AND PASSWORD = :oldPassword";
    String ACCOUNT_RESET_PASSWORD = "UPDATE ACCOUNT SET PASSWORD = :password WHERE ID = :id AND MODE = 'builtin'";
    String ACCOUNT_CHANGE_EMAIL = "UPDATE ACCOUNT SET EMAIL = :email WHERE ID = :id AND MODE = 'builtin' AND PASSWORD = :password";
    String ACCOUNT_CHANGE_LOCALE = "UPDATE ACCOUNT SET LOCALE = :locale WHERE ID = :id";
    // Project authorizations
    String PROJECT_AUTHORIZATION_DELETE = "DELETE FROM PROJECT_AUTHORIZATION WHERE PROJECT = :project AND ACCOUNT = :account";
    String PROJECT_AUTHORIZATION_INSERT = "INSERT INTO PROJECT_AUTHORIZATION (PROJECT, ACCOUNT, ROLE) VALUES (:project, :account, :role)";
    String PROJECT_AUTHORIZATION_BY_PROJECT = "SELECT * FROM PROJECT_AUTHORIZATION WHERE PROJECT = :project";
    String PROJECT_AUTHORIZATION_BY_ACCOUNT = "SELECT * FROM PROJECT_AUTHORIZATION WHERE ACCOUNT = :account";
    String EVENT_INSERT = "INSERT INTO EVENT (EVENT_CODE, EVENT_PARAMETERS, EVENT_TIMESTAMP, ACCOUNT_ID, ACCOUNT_NAME) VALUES (:code, :parameters, :timestamp, :accountId, :accountName)";
    String EVENT_SET_ENTITY = "UPDATE EVENT SET %s = :entityId WHERE ID = :id";
    // Branches
    String BRANCH_BY_ID = "SELECT * FROM BRANCH WHERE ID = :id";
    String BRANCH_CREATE = "INSERT INTO BRANCH (PROJECT, NAME) VALUES (:project, :name)";
    String BRANCH_BY_PROJECT = "SELECT * FROM BRANCH WHERE PROJECT = :project ORDER BY NAME";
    // Branch parameters
    String BRANCH_PARAMETER_REMOVE = "DELETE FROM BRANCH_PARAMETER WHERE BRANCH = :branch AND PARAMETER = :name";
    String BRANCH_PARAMETER_INSERT = "INSERT INTO BRANCH_PARAMETER (BRANCH, PARAMETER, VALUE) VALUES (:branch, :name, :value)";
    String BRANCH_PARAMETER_BY_BRANCH = "SELECT PARAMETER, VALUE FROM BRANCH_PARAMETER WHERE BRANCH = :branch";
    // Requests
    String REQUEST_CREATE = "INSERT INTO REQUEST (BRANCH, TXFILEEXCHANGE_ID, TXFILEEXCHANGE_CONFIG, STATUS) VALUES (:branch, :configId, :configNode, :status)";
    String REQUEST_BY_ID = "SELECT ID, BRANCH, VERSION, STATUS FROM REQUEST WHERE ID = :id";
}
