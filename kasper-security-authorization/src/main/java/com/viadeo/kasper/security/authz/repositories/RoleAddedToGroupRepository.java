package com.viadeo.kasper.security.authz.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_Role;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

public class RoleAddedToGroupRepository extends Repository<Group_has_Role> {

    private AuthorizationStorage authorizationStorage;

    public RoleAddedToGroupRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    @Override
    protected Optional<Group_has_Role> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSave(final Group_has_Role aggregate) {
        authorizationStorage.addRoleToGroup(aggregate);
    }

    @Override
    protected void doDelete(final Group_has_Role aggregate) {
        authorizationStorage.removeRoleFromGroup(aggregate);
    }

    public List<Role> getRolesForGroup(final Group group){
        return authorizationStorage.getRolesForGroup(group);
    }

    public List<Group> getAllGroupsForRole(final Role role){
        return authorizationStorage.getAllGroupsForRole(role);
    }
}
