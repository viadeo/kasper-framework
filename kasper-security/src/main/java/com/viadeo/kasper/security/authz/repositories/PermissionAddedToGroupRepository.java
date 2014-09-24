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
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.entities.relations.Group_has_Permission;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import static com.google.common.base.Preconditions.checkNotNull;

public class PermissionAddedToGroupRepository extends Repository<Group_has_Permission> {

    private AuthorizationStorage authorizationStorage;

    // ------------------------------------------------------------------------

    public PermissionAddedToGroupRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = checkNotNull(authorizationStorage);
    }

    // ------------------------------------------------------------------------

    @Override
    protected Optional<Group_has_Permission> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        DefaultKasperRelationId defaultKasperRelationId = (DefaultKasperRelationId) aggregateIdentifier;
        return Optional.of(authorizationStorage.getGroupHasPermission(defaultKasperRelationId.getSourceId(), defaultKasperRelationId.getTargetId()));
    }

    @Override
    protected void doSave(final Group_has_Permission aggregate) {
        final boolean added = authorizationStorage.addPermissionToGroup(aggregate);
        if (!added) {
            throw new KasperCommandException("Unable to add permission [" + aggregate.getPermission().toString() + "] to Group [" + aggregate.getGroup().getName() + "]");
        }
    }

    @Override
    protected void doDelete(final Group_has_Permission aggregate) {
        final boolean deleted = authorizationStorage.removePermissionFromGroup(aggregate);
        if (!deleted) {
            throw new KasperCommandException("Unable to delete permission [" + aggregate.getPermission().toString() + "] from Group [" + aggregate.getGroup().getName() + "]");
        }
    }
}
