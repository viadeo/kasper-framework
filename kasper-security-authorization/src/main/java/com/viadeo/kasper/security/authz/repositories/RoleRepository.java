package com.viadeo.kasper.security.authz.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

public class RoleRepository extends Repository<Role> {

    private AuthorizationStorage authorizationStorage;

    public RoleRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    @Override
    protected Optional<Role> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        return Optional.of(authorizationStorage.getRole(aggregateIdentifier));
    }

    @Override
    protected void doSave(final Role aggregate) {
        authorizationStorage.createRole(aggregate);
    }

    @Override
    protected void doDelete(final Role aggregate) {
        authorizationStorage.deleteRole(aggregate);
    }

    public List<Role> getAllRoles(){
        return authorizationStorage.getAllRoles();
    }
}
