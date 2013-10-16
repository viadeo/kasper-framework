// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.google.common.collect.Lists;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.event.Event;

import com.viadeo.kasper.test.exception.*;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class GivenClause extends Clause<GivenClause> {

    final List<PlatformFixture> fixtures = Lists.newArrayList();
    final List<EntityClauseComponent> entities = Lists.newArrayList();
    final List<Event> events = Lists.newArrayList();

    // ------------------------------------------------------------------------

    GivenClause(final PlatformFixture fixture) {
        super(fixture);
    }

    // ------------------------------------------------------------------------

    public GivenClause with (final PlatformFixture fixture) {
        if ( ! fixture.isEnsured()) {
            fixture.ensure();
        }
        this.fixtures.add(checkNotNull(fixture));
        return this;
    }

    // ------------------------------------------------------------------------

    public GivenClause mocked(final Class mockedClass) {
        // TODO
        return this;
    }

    // ------------------------------------------------------------------------

    public <ENTITY extends Entity> EntityClauseComponent<GivenClause, ENTITY> has(final Class<ENTITY> entityClass) {
        final EntityClauseComponent<GivenClause, ENTITY> entity = new EntityClauseComponent<>(this, entityClass);
        this.entities.add(entity);
        return entity;
    }

    public GivenClause sent(final Event event) {
        this.events.add(checkNotNull(event));
        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    boolean apply() {
        // TODO
        return true;
    }

}
