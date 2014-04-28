package com.viadeo.kasper.security.authz;

import com.viadeo.kasper.KasperID;

import java.util.List;

public class Group extends Subject {

    private String name;
    private List<User> users;
    private KasperID kasperId;

    public Group(final List<Role> roles,
                 final List<Permission> permissions,
                 final List<User> users,
                 final String name,
                 final KasperID kasperId) {
        super(roles, permissions);
        this.users = users;
        this.name = name;
        this.kasperId = kasperId;
    }

    public KasperID getKasperId() {
        return kasperId;
    }

    public void setKasperId(KasperID kasperId) {
        this.kasperId = kasperId;
    }
}
