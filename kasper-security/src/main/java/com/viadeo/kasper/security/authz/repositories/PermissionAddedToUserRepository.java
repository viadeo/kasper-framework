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
import com.viadeo.kasper.security.authz.entities.relations.User_has_Permission;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import static com.google.common.base.Preconditions.checkNotNull;

public class PermissionAddedToUserRepository extends Repository<User_has_Permission> {

    private AuthorizationStorage authorizationStorage;

    // ------------------------------------------------------------------------

    public PermissionAddedToUserRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = checkNotNull(authorizationStorage);
    }

    // ------------------------------------------------------------------------

    @Override
    protected Optional<User_has_Permission> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        DefaultKasperRelationId defaultKasperRelationId = (DefaultKasperRelationId) aggregateIdentifier;
        return Optional.of(authorizationStorage.getUserHasPermission(defaultKasperRelationId.getSourceId(), defaultKasperRelationId.getTargetId()));
    }

    @Override
    protected void doSave(final User_has_Permission aggregate) {
        final boolean added = authorizationStorage.addPermissionToUser(aggregate);
        if (!added) {
            throw new KasperCommandException("Unable to add permission [" + aggregate.getPermission().toString() + "] to User [" + aggregate.getUser().getLastName() + "]");
        }
    }

    @Override
    protected void doDelete(final User_has_Permission aggregate) {
        final boolean deleted = authorizationStorage.removePermissionFromUser(aggregate);
        if (!deleted) {
            throw new KasperCommandException("Unable to delete permission [" + aggregate.getPermission().toString() + "] from User [" + aggregate.getUser().getLastName() + "]");
        }
    }

}
