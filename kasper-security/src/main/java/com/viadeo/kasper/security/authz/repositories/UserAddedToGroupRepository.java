// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.repositories;

import com.google.common.base.Optional;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.command.exceptions.KasperCommandException;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.impl.DefaultKasperRelationId;
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
        DefaultKasperRelationId defaultKasperRelationId = (DefaultKasperRelationId) aggregateIdentifier;
        return Optional.of(authorizationStorage.getGroupHasUser(defaultKasperRelationId.getSourceId(), defaultKasperRelationId.getTargetId()));
    }

    @Override
    protected void doSave(final Group_has_User aggregate) {
        final boolean added = authorizationStorage.addUserToGroup(aggregate);
        if (!added) {
            throw new KasperCommandException("Unable to add User [" + aggregate.getUser().getLastName() + "] to Group [" + aggregate.getGroup().getName() + "]");
        }
    }

    @Override
    protected void doDelete(final Group_has_User aggregate) {
        final boolean deleted = authorizationStorage.removeUserFromGroup(aggregate);
        if (!deleted) {
            throw new KasperCommandException("Unable to delete User [" + aggregate.getUser().getLastName() + "] from Group [" + aggregate.getGroup().getName() + "]");
        }
    }

}
