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
import com.viadeo.kasper.security.authz.entities.permission.impl.Role;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class RoleRepository extends Repository<Role> {

    private AuthorizationStorage authorizationStorage;

    // ------------------------------------------------------------------------

    public RoleRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = checkNotNull(authorizationStorage);
    }

    // ------------------------------------------------------------------------

    @Override
    protected Optional<Role> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        return authorizationStorage.getRole(aggregateIdentifier);
    }

    @Override
    protected void doSave(final Role aggregate) {
        final boolean saved = authorizationStorage.createRole(aggregate);
        if (!saved) {
            throw new KasperCommandException("Unable to create Role [" + aggregate.getName() + "]");
        }
    }

    @Override
    protected void doDelete(final Role aggregate) {
        final boolean deleted = authorizationStorage.deleteRole(aggregate);
        if (!deleted) {
            throw new KasperCommandException("Unable to delete Role [" + aggregate.getName() + "]");
        }
    }

    public Optional<List<Role>> getAllRoles(){
        return authorizationStorage.getAllRoles();
    }

}
