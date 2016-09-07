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
package com.viadeo.kasper.core.component.event.saga;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.saga.factory.SagaFactory;
import com.viadeo.kasper.core.component.event.saga.repository.SagaRepository;
import com.viadeo.kasper.core.component.event.saga.step.Step;
import com.viadeo.kasper.core.metrics.KasperMetrics;

import java.lang.reflect.Method;
import java.util.Set;

public class MeasuredSagaExecutor<SAGA extends Saga> extends SagaExecutor<SAGA>{

    private final MetricRegistry metricRegistry;
    private final String errorsName;
    private final String requestHandleTimeName;

    public MeasuredSagaExecutor(
            MetricRegistry metricRegistry,
            Class<SAGA> sagaClass,
            Set<Step> steps,
            SagaFactory factory,
            SagaRepository repository
    ) {
        super(sagaClass, steps, factory, repository);
        this.metricRegistry = metricRegistry;

        requestHandleTimeName = KasperMetrics.name(sagaClass, "request-handle-time");
        errorsName = KasperMetrics.name(sagaClass, "errors");
    }

    @Override
    public void execute(Object sagaIdentifier, String methodName, boolean endAfterExecution) {
        Timer.Context requestTimer = metricRegistry.timer(requestHandleTimeName).time();
        try {
            super.execute(sagaIdentifier, methodName, endAfterExecution);
        } catch (Exception e) {
            metricRegistry.meter(errorsName).mark();
            throw e;
        } finally {
            requestTimer.stop();
        }
    }

    @Override
    public void execute(Context context, Event event) {
        Timer.Context requestTimer = metricRegistry.timer(requestHandleTimeName).time();
        try {
            super.execute(context, event);
        } catch (Exception e) {
            metricRegistry.meter(errorsName).mark();
            throw e;
        } finally {
            requestTimer.stop();
        }
    }

    @Override
    protected void invokeMethod(Object sagaIdentifier, String methodName, Method method, Saga saga) {
        Timer.Context invokeTimer = metricRegistry.timer(KasperMetrics.name(saga.getClass(), methodName + ".invoke-handle-time")).time();
        try {
            super.invokeMethod(sagaIdentifier, methodName, method, saga);
        } catch (Exception e) {
            metricRegistry.meter(KasperMetrics.name(saga.getClass(), methodName + ".errors")).mark();
            throw e;
        } finally {
            invokeTimer.stop();
        }
    }

    @Override
    protected void invokeStep(Context context, Event event, Step step, Object sagaIdentifier, Saga saga) {
        Timer.Context invokeTimer = metricRegistry.timer(KasperMetrics.name(saga.getClass(), step.name() + ".invoke-handle-time")).time();
        try {
            super.invokeStep(context, event, step, sagaIdentifier, saga);
        } catch (Exception e) {
            metricRegistry.meter(KasperMetrics.name(saga.getClass(), saga.getClass() + ".errors")).mark();
            throw e;
        } finally {
            invokeTimer.stop();
        }
    }

    @Override
    protected void persistSaga(Object sagaIdentifier, Saga saga, boolean endSaga) {
        Timer.Context persistTimer = metricRegistry.timer(KasperMetrics.name(saga.getClass(), "persist-handle-time")).time();
        try {
            super.persistSaga(sagaIdentifier, saga, endSaga);
        } finally {
            persistTimer.stop();
        }
    }
}
