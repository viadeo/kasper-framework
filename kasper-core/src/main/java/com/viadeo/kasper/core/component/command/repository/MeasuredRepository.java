// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.repository;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 * This implementation of <code>Repository</code> allows to add metrics.
 *
 * @param <ID> the aggregate id
 * @param <AGR> the aggregate
 */
public class MeasuredRepository<ID extends KasperID, AGR extends AggregateRoot>
        extends AbstractRepository<ID, AGR>
        implements Repository<ID, AGR>
{

    private static final String GLOBAL_METER_SAVES_NAME = name(Repository.class, "saves");
    private static final String GLOBAL_METER_SAVE_ERRORS_NAME = name(Repository.class, "save-errors");

    private static final String GLOBAL_METER_LOADS_NAME = name(Repository.class, "loads");
    private static final String GLOBAL_METER_LOAD_ERRORS_NAME = name(Repository.class, "load-errors");

    private static final String GLOBAL_METER_DELETES_NAME = name(Repository.class, "deletes");
    private static final String GLOBAL_METER_DELETE_ERRORS_NAME = name(Repository.class, "delete-errors");

    // ------------------------------------------------------------------------

    private final String timerSaveTimeName;
    private final String meterSaveErrorsName;
    private final String timerLoadTimeName;
    private final String meterLoadErrorsName;
    private final String timerDeleteTimeName;
    private final String meterDeleteErrorsName;

    private final MetricRegistry metricRegistry;
    private final AbstractRepository<ID,AGR> repository;

    // ------------------------------------------------------------------------

    public MeasuredRepository(final MetricRegistry metricRegistry, final AbstractRepository<ID,AGR> repository) {
        super(metricRegistry, repository.eventStore, repository.eventBus);
        this.metricRegistry = checkNotNull(metricRegistry);
        this.repository = checkNotNull(repository);

        this.timerSaveTimeName = name(repository.getRepositoryClass(), "save-time");
        this.meterSaveErrorsName = name(repository.getRepositoryClass(), "save-errors");
        this.timerLoadTimeName = name(repository.getRepositoryClass(), "load-time");
        this.meterLoadErrorsName = name(repository.getRepositoryClass(), "load-errors");
        this.timerDeleteTimeName = name(repository.getRepositoryClass(), "delete-time");
        this.meterDeleteErrorsName = name(repository.getRepositoryClass(), "delete-errors");
    }

    // ------------------------------------------------------------------------

    protected AxonRepositoryFacade<AGR> createAxonRepository(
            final MetricRegistry metricRegistry,
            final AbstractRepository<ID,AGR> repository
    ) {
        return null;
    }

    @Override
    protected void doUpdate(final AGR aggregate) {
        final Timer.Context timer = metricRegistry.timer(timerSaveTimeName).time();

        try {

            repository.doUpdate(aggregate);

        } catch (final Exception exception) {
            metricRegistry.meter(GLOBAL_METER_SAVE_ERRORS_NAME).mark();
            metricRegistry.meter(meterSaveErrorsName).mark();
            throw exception;

        } finally {
            timer.stop();
            metricRegistry.meter(GLOBAL_METER_SAVES_NAME).mark();
        }
    }

    @Override
    protected void doSave(final AGR aggregate) {
        final Timer.Context timer = metricRegistry.timer(timerSaveTimeName).time();

        try {

            repository.doSave(aggregate);

        } catch (final Exception exception) {
            metricRegistry.meter(GLOBAL_METER_SAVE_ERRORS_NAME).mark();
            metricRegistry.meter(meterSaveErrorsName).mark();
            throw exception;

        } finally {
            timer.stop();
            metricRegistry.meter(GLOBAL_METER_SAVES_NAME).mark();
        }
    }

    @Override
    protected Optional<AGR>  doLoad(final ID aggregateIdentifier, final Long expectedVersion) {
        final Timer.Context timer = metricRegistry.timer(timerLoadTimeName).time();

        final Optional<AGR> agr;

        try {

            agr = repository.doLoad(aggregateIdentifier, expectedVersion);

        } catch (final Exception exception) {
            metricRegistry.meter(GLOBAL_METER_LOAD_ERRORS_NAME).mark();
            metricRegistry.meter(meterLoadErrorsName).mark();
            throw exception;

        } finally {
            timer.stop();
            metricRegistry.meter(GLOBAL_METER_LOADS_NAME).mark();
        }

        return agr;
    }

    @Override
    protected void doDelete(final AGR aggregate) {
        final Timer.Context timer = metricRegistry.timer(timerDeleteTimeName).time();

        try {

            repository.doDelete(aggregate);

        } catch (final Exception exception) {
            metricRegistry.meter(GLOBAL_METER_DELETE_ERRORS_NAME).mark();
            metricRegistry.meter(meterDeleteErrorsName).mark();
            throw exception;

        } finally {
            timer.stop();
            metricRegistry.meter(GLOBAL_METER_DELETES_NAME).mark();
        }
    }

    @Override
    public Class<AGR> getAggregateClass() {
        return repository.getAggregateClass();
    }

    @Override
    public void save(final AGR aggregate) {
        repository.save(aggregate);
    }

    @Override
    public Optional<AGR> load(final ID aggregateIdentifier, final Long expectedVersion) {
        return repository.load(aggregateIdentifier, expectedVersion);
    }

    @Override
    public Optional<AGR> load(final ID aggregateIdentifier) {
        return repository.load(aggregateIdentifier);
    }

    @Override
    public void delete(final AGR aggregate) {
        repository.delete(aggregate);
    }

    @Override
    public void add(final AGR aggregate) {
        repository.add(aggregate);
    }

    @Override
    public boolean has(final ID id) {
        return repository.has(id);
    }

    @Override
    public Optional<AGR> get(final ID aggregateIdentifier, final Long expectedVersion) {
        return repository.get(aggregateIdentifier, expectedVersion);
    }

    @Override
    public Optional<AGR> get(final ID aggregateIdentifier) {
        return repository.get(aggregateIdentifier);
    }

    @Override
    public Class<?> getRepositoryClass() {
        return repository.getRepositoryClass();
    }

    @Override
    public void checkAggregateIdentifier(ID aggregateIdentifier) {
        repository.checkAggregateIdentifier(aggregateIdentifier);
    }
}
