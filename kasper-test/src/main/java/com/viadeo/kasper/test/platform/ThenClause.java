// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.viadeo.kasper.ddd.Entity;
import com.viadeo.kasper.event.Event;

import java.util.List;
import java.util.Map;

public class ThenClause extends Clause<ThenClause> {

    public enum EntityState { CREATED, UPDATED, DELETED };
    private final Multimap<EntityState, EntityClauseComponent<ThenClause, Entity>> entities = ArrayListMultimap.create();

    private final Map<String, ResultThenClauseComponent> results = Maps.newHashMap();
    private final List<Event> events = Lists.newArrayList();

    // ------------------------------------------------------------------------

    ThenClause(final PlatformFixture fixture) {
        super(fixture);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public <ENTITY extends Entity> EntityClauseComponent<ThenClause, ENTITY> assertUpdated(final Class<ENTITY> entityClass) {
        final EntityClauseComponent<ThenClause, ENTITY> entityClause = new EntityClauseComponent<>(this, entityClass);
        entities.put(EntityState.UPDATED, (EntityClauseComponent<ThenClause, Entity>) entityClause);
        return entityClause;
    }

    @SuppressWarnings("unchecked")
    public <ENTITY extends Entity> EntityClauseComponent<ThenClause, ENTITY> assertAdded(final Class<ENTITY> entityClass) {
        final EntityClauseComponent<ThenClause, ENTITY> entityClause = new EntityClauseComponent<>(this, entityClass);
        entities.put(EntityState.CREATED, (EntityClauseComponent<ThenClause, Entity>) entityClause);
        return entityClause;
    }

    @SuppressWarnings("unchecked")
    public <ENTITY extends Entity> EntityClauseComponent<ThenClause, ENTITY> assertDeleted(final Class<ENTITY> entityClass) {
        final EntityClauseComponent<ThenClause, ENTITY> entityClause = new EntityClauseComponent<>(this, entityClass);
        entities.put(EntityState.DELETED, (EntityClauseComponent<ThenClause, Entity>) entityClause);
        return entityClause;
    }

    public ThenClause assertEvent(final Event event) {
        this.events.add(event);
        return this;
    }

    public ResultThenClauseComponent assertResult(final String resultName) {
        final ResultThenClauseComponent resultClause = new ResultThenClauseComponent(this);
        results.put(resultName, resultClause);
        return resultClause;
    }

    // ------------------------------------------------------------------------

    public ThenClause withNoOtherUpdates() {
        // TODO
        return this;
    }

    public ThenClause withNoOtherAdds() {
        // TODO
        return this;
    }

    public ThenClause withNoOtherEvents() {
        // TODO
        return this;
    }

    public ThenClause withNoOtherListenedEvents() {
        // TODO
        return this;
    }

    public ThenClause withNoOtherResult() {
        // TODO
        return this;
    }

    public ThenClause withNoOtherEntryForResult(final String resultName) {
        // TODO
        return this;
    }

    // ------------------------------------------------------------------------

    @Override
    boolean apply() {
        // TODO
        return true;
    }

}
