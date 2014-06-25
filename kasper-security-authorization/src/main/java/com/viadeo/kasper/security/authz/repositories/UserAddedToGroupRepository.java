package com.viadeo.kasper.security.authz.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_User;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

public class UserAddedToGroupRepository extends Repository<Group_has_User> {

    private AuthorizationStorage authorizationStorage;

    public UserAddedToGroupRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    @Override
    protected Optional<Group_has_User> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doSave(final Group_has_User aggregate) {
        authorizationStorage.addUserToGroup(aggregate);
    }

    @Override
    protected void doDelete(final Group_has_User aggregate) {
        authorizationStorage.removeUserFromGroup(aggregate);
    }
}
