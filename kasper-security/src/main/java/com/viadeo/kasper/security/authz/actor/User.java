package com.viadeo.kasper.security.authz.actor;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperId;
import com.viadeo.kasper.security.authz.permission.Permission;
import com.viadeo.kasper.security.authz.permission.impl.Role;

import java.util.List;

public class User extends Subject {

    private KasperID kasperId;

    public User() {
        super();
        this.kasperId = new DefaultKasperId();
    }

    public User(final KasperID kasperId) {
        super();
        this.kasperId = kasperId;
    }

    public User(final List<Role> roles, final List<Permission> permissions) {
        super(roles, permissions);
        this.kasperId = new DefaultKasperId();
    }

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
