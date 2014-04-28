package com.viadeo.kasper.security.authz;

import com.viadeo.kasper.KasperID;

import java.util.List;

public class User extends Subject {

    private KasperID kasperId;

    public User(final List<Role> roles, final List<Permission> permissions, final KasperID kasperId) {
        super(roles, permissions);
        this.kasperId = kasperId;
    }

    public KasperID getKasperId() {
        return kasperId;
    }

    public void setKasperId(KasperID kasperId) {
        this.kasperId = kasperId;
    }
}
