// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.use_case_1;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.QueryAnswer;
import com.viadeo.kasper.cqrs.query.QueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryAnswer;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryCollectionAnswer;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryService;
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.event.impl.AbstractEventListener;

import java.util.Collection;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.test.platform.use_case_1.TestEntity.TestCreatedEvent;
import static com.viadeo.kasper.test.platform.use_case_1.TestEntity.TestNameChangedEvent;

@XKasperQueryService( domain = TestDomain.class )
public class TestGetAllEntitiesQueryService
        extends AbstractQueryService<
                        TestGetAllEntitiesQueryService.TestGetAllEntitiesQuery,
                        TestGetAllEntitiesQueryService.TestGetAllEntitiesQueryAnswer
        >
{

    private static final ConcurrentMap<KasperID, String> index = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    @XKasperQuery
    public static final class TestGetAllEntitiesQuery implements Query { }

    public static final class TestEntityAnswer implements QueryAnswer {

        private final KasperID id;
        private final String name;

        public TestEntityAnswer(final KasperID id, final String name) {
            this.id = checkNotNull(id);
            this.name = checkNotNull(name);
        }

        public KasperID getId() {
            return this.id;
        }

        public String getName() {
            return this.name;
        }

    }

    @XKasperQueryAnswer
    public static final class TestGetAllEntitiesQueryAnswer extends AbstractQueryCollectionAnswer<TestEntityAnswer> {
        // A collection of TestEntityAnswer
    }

    // ------------------------------------------------------------------------

    @XKasperEventListener( domain = TestDomain.class )
    public static final class TestCreatedEventListener extends AbstractEventListener<TestCreatedEvent> {
        @Override
        public void handle(final TestCreatedEvent event){
            index.put(event.getId(), event.getName());
        }
    }

    @XKasperEventListener( domain = TestDomain.class )
    public static final class TestNameChangedEventListener extends AbstractEventListener<TestNameChangedEvent> {
        @Override
        public void handle(final TestNameChangedEvent event) {
            assert(index.containsKey(event.getId()));
            index.put(event.getId(), event.getNewName());
        }
    }

    // ------------------------------------------------------------------------

    public QueryResult<TestGetAllEntitiesQueryAnswer> retrieve(final TestGetAllEntitiesQuery query) throws Exception {
        final Collection<TestEntityAnswer> answers = Lists.newArrayList();

        for(final KasperID id : index.keySet()) {
            answers.add(new TestEntityAnswer(id, index.get(id)));
        }

        final TestGetAllEntitiesQueryAnswer answer = new TestGetAllEntitiesQueryAnswer().withList(answers);
        return QueryResult.of(answer);
    }

}
