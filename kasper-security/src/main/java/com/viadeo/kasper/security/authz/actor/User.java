// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.actor;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class User extends Subject {

    private KasperID kasperId;

    // ------------------------------------------------------------------------

    public User() {
        super();
        this.kasperId = new DefaultKasperId();
    }

    public User(final KasperID kasperId) {
        super();
        this.kasperId = checkNotNull(kasperId);
    }

    public User(final List<Role> roles, final List<Permission> permissions) {
        super(roles, permissions);
        this.kasperId = new DefaultKasperId();
    }

    public User(final List<Role> roles, final List<Permission> permissions, final KasperID kasperId) {
        super(roles, permissions);
        this.kasperId = checkNotNull(kasperId);
    }

    // ------------------------------------------------------------------------

    public KasperID getKasperId() {
        return kasperId;
    }

    public void setKasperId(final KasperID kasperId) {
        this.kasperId = checkNotNull(kasperId);
    }

}
