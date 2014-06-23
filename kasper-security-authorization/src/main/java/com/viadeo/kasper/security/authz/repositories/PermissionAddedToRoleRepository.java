package com.viadeo.kasper.security.authz.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.relations.Role_has_Permission;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

public class PermissionAddedToRoleRepository extends Repository<Role_has_Permission> {

    private AuthorizationStorage authorizationStorage;

    public PermissionAddedToRoleRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    @Override
    protected Optional<Role_has_Permission> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSave(final Role_has_Permission aggregate) {
        authorizationStorage.addPermissionToRole(aggregate);
    }

    @Override
    protected void doDelete(final Role_has_Permission aggregate) {
        authorizationStorage.removePermissionFromRole(aggregate);
    }

    public List<Permission> getPermissionsForRole(final Role role){
        return authorizationStorage.getPermissionsForRole(role);
    }
}
