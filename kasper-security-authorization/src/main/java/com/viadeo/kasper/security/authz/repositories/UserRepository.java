package com.viadeo.kasper.security.authz.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.security.authz.entities.actor.User;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

public class UserRepository extends Repository<User> {

    private AuthorizationStorage authorizationStorage;

    public UserRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = authorizationStorage;
    }

    @Override
    protected Optional<User> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        return Optional.of(authorizationStorage.getUser(aggregateIdentifier));
    }

    @Override
    protected void doSave(final User aggregate) {
        authorizationStorage.createUser(aggregate);
    }

    @Override
    protected void doDelete(final User aggregate) {
        authorizationStorage.deleteUser(aggregate);
    }

    public List<User> getAllUsers(){
        return authorizationStorage.getAllUsers();
    }
}
