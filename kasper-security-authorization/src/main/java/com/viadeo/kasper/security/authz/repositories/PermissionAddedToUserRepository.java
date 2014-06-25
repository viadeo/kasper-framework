package com.viadeo.kasper.security.authz.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.relations.User_has_Permission;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

public class PermissionAddedToUserRepository extends Repository<User_has_Permission> {

    private AuthorizationStorage authorizationStorage;

    public PermissionAddedToUserRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    @Override
    protected Optional<User_has_Permission> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSave(final User_has_Permission aggregate) {
        authorizationStorage.addPermissionToUser(aggregate);
    }

    @Override
    protected void doDelete(final User_has_Permission aggregate) {
        authorizationStorage.removePermissionFromUser(aggregate);
    }
}
