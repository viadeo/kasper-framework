package com.viadeo.kasper.security.authz.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.permission.Permission;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_Permission;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

public class PermissionAddedToGroupRepository extends Repository<Group_has_Permission> {

    private AuthorizationStorage authorizationStorage;

    public PermissionAddedToGroupRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    @Override
    protected Optional<Group_has_Permission> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSave(final Group_has_Permission aggregate) {
        authorizationStorage.addPermissionToGroup(aggregate);
    }

    @Override
    protected void doDelete(final Group_has_Permission aggregate) {
        authorizationStorage.removePermissionFromGroup(aggregate);
    }

    public List<Permission> getPermissionsForGroup(final Group group){
        return authorizationStorage.getPermissionsForGroup(group);
    }
}
