// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.repository;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.saga.Saga;
import com.viadeo.kasper.event.saga.SagaMapper;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base sagaRepository implementation that uses {@link com.viadeo.kasper.event.saga.SagaMapper}
 */
public abstract class BaseSagaRepository implements SagaRepository {

    private final SagaMapper sagaMapper;

    // ------------------------------------------------------------------------

    public BaseSagaRepository(final SagaMapper sagaMapper) {
        this.sagaMapper = checkNotNull(sagaMapper);
    }

    // ------------------------------------------------------------------------

    @Override
    public final Optional<Saga> load(Object identifier) {
        checkNotNull(identifier);

        final Map<String,Object> properties = doLoad(identifier);
        if ( null != properties ) {
            return Optional.fromNullable(sagaMapper.to(properties));
        }

        return Optional.absent();
    }

    @Override
    public final void save(final Object identifier, final Saga saga) {
        checkNotNull(identifier);
        checkNotNull(saga);

        final Map<String, Object> properties = sagaMapper.from(identifier, saga);

        doSave(identifier, properties);
    }

    // ------------------------------------------------------------------------

    public abstract Map<String, Object> doLoad(Object identifier);

    public abstract void doSave(Object identifier, Map<String, Object> sagaProperties);

}
