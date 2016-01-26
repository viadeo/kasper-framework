// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.saga.repository;

import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.viadeo.kasper.core.component.event.saga.Saga;
import com.viadeo.kasper.core.component.event.saga.SagaMapper;
import com.viadeo.kasper.core.component.event.saga.exception.SagaPersistenceException;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base sagaRepository implementation that uses {@link com.viadeo.kasper.core.component.event.saga.SagaMapper}
 */
public abstract class BaseSagaRepository implements SagaRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseSagaRepository.class);

    private final SagaMapper sagaMapper;

    // ------------------------------------------------------------------------

    public BaseSagaRepository(final SagaMapper sagaMapper) {
        this.sagaMapper = checkNotNull(sagaMapper);
    }

    // ------------------------------------------------------------------------

    @Override
    public <SAGA extends Saga> Optional<SAGA> load(Class<SAGA> sagaClass, Object identifier) throws SagaPersistenceException {
        checkNotNull(identifier);
        checkNotNull(sagaClass);

        final Map<String,String> properties;

        try {
            properties = doLoad(sagaClass, identifier);
        } catch (SagaPersistenceException e) {
            throw Throwables.propagate(e);
        }

        if (properties == null || properties.isEmpty()) {
            LOGGER.debug("Failed to load a saga instance with '{}' as identifier : no related data", identifier);
            return Optional.absent();
        }

        final Object sagaClassAsString = properties.get(SagaMapper.X_KASPER_SAGA_CLASS);

        if (sagaClassAsString == null) {
            throw new SagaPersistenceException(
                    String.format("Failed to load a saga instance with '%s' as identifier : saga type is not specified, <properties=%s>", identifier, properties)
            );
        }

        if(!sagaClass.getName().equals(sagaClassAsString)) {
            throw new SagaPersistenceException(
                    String.format("Failed to load a saga instance with '%s' as identifier : mismatch saga type between %s and %s, <saga=%s> <properties=%s>", identifier, sagaClass.getName(), sagaClassAsString, sagaClassAsString, properties)
            );
        }

        return Optional.fromNullable(sagaMapper.to(sagaClass, identifier, properties));
    }

    @Override
    public final void save(Object identifier, Saga saga) throws SagaPersistenceException {
        checkNotNull(identifier);
        checkNotNull(saga);

        Timer.Context mappingTimer = KasperMetrics.getMetricRegistry().timer(KasperMetrics.name(saga.getClass(), "mapping-handle-time")).time();

        final Map<String, String> properties = sagaMapper.from(identifier, saga);

        mappingTimer.stop();

        Timer.Context savingTimer = KasperMetrics.getMetricRegistry().timer(KasperMetrics.name(saga.getClass(), "saving-handle-time")).time();

        doSave(saga.getClass(), identifier, properties);

        savingTimer.stop();
    }

    @Override
    public void initStoreFor(final Class<? extends Saga> sagaClass) {
        sagaMapper.getOrCreateMappingDescriptor(sagaClass);
    }

    // ------------------------------------------------------------------------

    public abstract Map<String, String> doLoad(Class<? extends Saga> sagaClass, Object identifier) throws SagaPersistenceException;

    public abstract void doSave(Class<? extends Saga> sagaClass, Object identifier, Map<String, String> sagaProperties) throws SagaPersistenceException;

}
