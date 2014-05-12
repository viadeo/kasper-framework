// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.authz.actor;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;

import java.util.List;


import static com.google.common.base.Preconditions.checkNotNull;


public class Group extends Subject {

    private String name;
    private List<User> users;
    private KasperID kasperId;


    // ------------------------------------------------------------------------

    public Group(final List<Role> roles,
                 final List<Permission> permissions,
                 final List<User> users,
                 final String name,
                 final KasperID kasperId) {
        super(roles, permissions);
        this.users = checkNotNull(users);
        this.name = checkNotNull(name);
        this.kasperId = checkNotNull(kasperId);
    }

    // ------------------------------------------------------------------------

    public KasperID getKasperId() {
        return kasperId;
    }

    public void setKasperId(final KasperID kasperId) {
        this.kasperId = checkNotNull(kasperId);
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = checkNotNull(name);
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(final List<User> users) {
        this.users = checkNotNull(users);
    }
}
