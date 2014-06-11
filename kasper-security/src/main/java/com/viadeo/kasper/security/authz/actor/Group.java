// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.security.authz.actor;

import com.google.common.collect.Lists;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;

import java.util.ArrayList;
import java.util.List;


import static com.google.common.base.Preconditions.checkNotNull;


public class Group extends Actor {

    private String name;
    private List<User> users;


    // ------------------------------------------------------------------------

    public Group(final String name){
        super();
        this.name = checkNotNull(name);
        this.users = Lists.newArrayList();
    }

    public Group(final String name,
                 final List<Role> roles,
                 final List<Permission> permissions,
                 final List<User> users) {
        super(roles, permissions);
        this.users = checkNotNull(users);
        this.name = checkNotNull(name);
    }

    // ------------------------------------------------------------------------

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

    public void addUser(final User user){
        users.add(checkNotNull(user));
    }

    public void removeUser(final User user){
        users.remove(checkNotNull(user));
    }
}
