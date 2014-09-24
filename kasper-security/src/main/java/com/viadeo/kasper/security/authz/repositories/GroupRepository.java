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
import com.viadeo.kasper.security.authz.entities.actor.Group;
import com.viadeo.kasper.security.authz.storage.AuthorizationStorage;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class GroupRepository extends Repository<Group> {

    private AuthorizationStorage authorizationStorage;

    // ------------------------------------------------------------------------

    public GroupRepository(final AuthorizationStorage authorizationStorage) {
        this.authorizationStorage = checkNotNull(authorizationStorage);
    }

    // ------------------------------------------------------------------------

    @Override
    protected Optional<Group> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
        return authorizationStorage.getGroup(aggregateIdentifier);
    }

    @Override
    protected void doSave(final Group aggregate) {
        final boolean saved = authorizationStorage.createGroup(aggregate);
        if (!saved) {
            throw new KasperCommandException("Unable to create Group [" + aggregate.getName() + "]");
        }
    }

    @Override
    protected void doDelete(final Group aggregate) {
        final boolean deleted = authorizationStorage.deleteGroup(aggregate);
        if (!deleted) {
            throw new KasperCommandException("Unable to delete Group [" + aggregate.getName() + "]");
        }
    }

    public List<Group> getAllGroups() {
        return authorizationStorage.getAllGroups();
    }

}
