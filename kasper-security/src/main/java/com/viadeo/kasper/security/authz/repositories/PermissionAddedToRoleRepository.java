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
import com.viadeo.kasper.security.authz.entities.relations.Role_has_Permission;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import static com.google.common.base.Preconditions.checkNotNull;

public class PermissionAddedToRoleRepository extends Repository<Role_has_Permission> {

    private AuthorizationStorage authorizationStorage;

    // ------------------------------------------------------------------------

    public PermissionAddedToRoleRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = checkNotNull(authorizationStorage);
    }

    // ------------------------------------------------------------------------

    @Override
    protected Optional<Role_has_Permission> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        DefaultKasperRelationId defaultKasperRelationId = (DefaultKasperRelationId) aggregateIdentifier;
        return Optional.of(authorizationStorage.getRoleHasPermission(defaultKasperRelationId.getSourceId(), defaultKasperRelationId.getTargetId()));
    }

    @Override
    protected void doSave(final Role_has_Permission aggregate) {
        final boolean added = authorizationStorage.addPermissionToRole(aggregate);
        if (!added) {
            throw new KasperCommandException("Unable to add permission [" + aggregate.getPermission().toString() + "] to Role [" + aggregate.getRole().getName() + "]");
        }

    }

    @Override
    protected void doDelete(final Role_has_Permission aggregate) {
        final boolean deleted = authorizationStorage.removePermissionFromRole(aggregate);
        if (!deleted) {
            throw new KasperCommandException("Unable to delete permission [" + aggregate.getPermission().toString() + "] from Role [" + aggregate.getRole().getName() + "]");
        }
    }

}
