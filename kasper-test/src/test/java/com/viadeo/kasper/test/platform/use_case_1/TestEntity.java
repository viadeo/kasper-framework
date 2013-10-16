// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.use_case_1;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.ddd.annotation.XKasperRepository;
import com.viadeo.kasper.ddd.impl.AbstractAggregateRoot;
import com.viadeo.kasper.ddd.impl.Repository;
import com.viadeo.kasper.er.annotation.XKasperConcept;
import com.viadeo.kasper.event.annotation.XKasperEvent;
import com.viadeo.kasper.event.domain.impl.AbstractDomainEvent;
import org.axonframework.eventhandling.annotation.EventHandler;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

@XKasperConcept( domain = TestDomain.class, label = "TestEntity")
public class TestEntity extends AbstractAggregateRoot {

    private String name;

    private final transient Context context;

    // ------------------------------------------------------------------------

    @XKasperEvent( action = "created" )
    public static final class TestCreatedEvent extends AbstractDomainEvent<TestDomain> {

        private KasperID id;
        private String name;

        public TestCreatedEvent(final Context context, final KasperID id, final String name) {
            super(context);
            this.id = id;
            this.name = name;
        }

        public KasperID getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    @XKasperEvent( action = "changed" )
    public static final class TestNameChangedEvent extends AbstractDomainEvent<TestDomain> {

        private final KasperID id;
        private final String oldName;
        private final String newName;

        public TestNameChangedEvent(Context context, KasperID id, String oldName, String newName) {
            super(context);
            this.id = checkNotNull(id);
            this.oldName = checkNotNull(oldName);
            this.newName = checkNotNull(newName);
        }

        public KasperID getId() {
            return this.id;
        }

        public String getOldName() {
            return this.oldName;
        }

        public String getNewName() {
            return this.newName;
        }

    }

    // ------------------------------------------------------------------------

    @XKasperRepository
    public static final class TestEntityRepository extends Repository<TestEntity> {

        private static final ConcurrentMap<KasperID, TestEntity> store = Maps.newConcurrentMap();

        // ------------------------------------------------------------------------

        @Override
        protected Optional<TestEntity> doLoad(final KasperID aggregateIdentifier, final Long expectedVersion) {
            return Optional.fromNullable(store.get(aggregateIdentifier));
        }

        @Override
        protected void doSave(final TestEntity aggregate) {
            store.putIfAbsent(aggregate.getEntityId(), aggregate);
        }

        @Override
        protected void doDelete(final TestEntity aggregate) {
            store.remove(aggregate.getEntityId());
        }

    }

    // ------------------------------------------------------------------------

    public TestEntity(final Context context, final KasperID id, final String name) {
        this.context = context;
        this.apply(new TestCreatedEvent(context, id, name));
    }

    public void changeName(final String name) {
        this.apply(new TestNameChangedEvent(context, this.getEntityId(), this.name, name));
    }

    // ------------------------------------------------------------------------

    @EventHandler
    protected void onTestCreatedEvent(final TestCreatedEvent event) {
        this.setId(event.getId());
        this.name = event.getName();
    }

    @EventHandler
    protected void onTestNameChangedEvent(final TestNameChangedEvent event) {
        this.name = event.getNewName();
    }

}
