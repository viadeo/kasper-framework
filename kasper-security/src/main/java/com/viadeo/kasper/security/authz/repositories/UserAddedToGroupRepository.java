// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_User;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import static com.google.common.base.Preconditions.checkNotNull;

public class UserAddedToGroupRepository extends Repository<Group_has_User> {

    private AuthorizationStorage authorizationStorage;

    // ------------------------------------------------------------------------

    public UserAddedToGroupRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = checkNotNull(authorizationStorage);
    }

    // ------------------------------------------------------------------------

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
