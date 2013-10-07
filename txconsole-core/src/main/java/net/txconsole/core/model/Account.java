package net.txconsole.core.model;

import com.google.common.base.Function;
import lombok.Data;
import net.txconsole.core.security.SecurityFunction;
import net.txconsole.core.security.SecurityRoles;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Data
public class Account {

    public static final Function<Account, String> emailFn = new Function<Account, String>() {
        @Override
        public String apply(Account o) {
            return o.getEmail();
        }
    };
    private final int id;
    private final String name;
    private final String fullName;
    private final String email;
    private final String roleName;
    private final String mode;
    private Locale locale;
    private Set<ACL> acls;

    public Account(int id, String name, String fullName, String email, String roleName, String mode, Locale locale) {
        this.id = id;
        this.name = name;
        this.fullName = fullName;
        this.email = email;
        this.roleName = roleName;
        this.mode = mode;
        this.locale = locale;
    }

    public boolean isGranted(SecurityFunction fn, int id) {
        return (SecurityRoles.ADMINISTRATOR.equals(roleName)) || (acls != null && acls.contains(new ACL(fn, id)));
    }

    public Account withACL(SecurityFunction fn, int id) {
        return withACL(new ACL(fn, id));
    }

    protected Account withACL(ACL acl) {
        if (acls == null) {
            acls = new HashSet<>();
        }
        acls.add(acl);
        return this;
    }
}
