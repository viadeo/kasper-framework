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
package com.viadeo.kasper.core.component;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.metrics.MetricNameStyle;
import com.viadeo.kasper.core.metrics.MetricNames;
import org.axonframework.repository.ConflictingAggregateVersionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This implementation of <code>Handler</code> allows to add metrics.
 * 
 * @param <INPUT> the input class handled by this <code>Handler</code>.
 * @param <MESSAGE> the message sent to this <code>Handler</code>.
 * @param <RESPONSE> the response returned by this <code>Handler</code>.
 *
 * @see Handler
 */
public abstract class MeasuredHandler<INPUT, MESSAGE extends KasperMessage<INPUT>, RESPONSE extends KasperResponse, HANDLER extends Handler<MESSAGE, RESPONSE,INPUT>>
        implements Handler<MESSAGE, RESPONSE, INPUT>
{

    protected final HANDLER handler;
    private final MetricRegistry metricRegistry;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private MetricNames inputMetricNames;
    private MetricNames domainMetricNames;
    private MetricNames globalMetricNames;

    public MeasuredHandler(
            final MetricRegistry metricRegistry,
            final HANDLER handler,
            final Class<?> globalComponent
    ) {
        this.metricRegistry = checkNotNull(metricRegistry);
        this.handler = checkNotNull(handler);
        this.globalMetricNames = instantiateGlobalMetricNames(checkNotNull(globalComponent));
    }

    protected MetricNames instantiateGlobalMetricNames(Class<?> componentClass) {
        return MetricNames.of(componentClass);
    }

    protected MetricNames instantiateInputMetricNames() {
        return MetricNames.of(handler.getInputClass());
    }

    protected MetricNames instantiateDomainMetricNames() {
        return MetricNames.byDomainOf(handler.getInputClass());
    }

    private MetricNames getOrInstantiateInputMetricNames() {
        if (inputMetricNames == null) {
            inputMetricNames = instantiateInputMetricNames();
        }
        return inputMetricNames;
    }

    private MetricNames getOrInstantiateDomainMetricNames() {
        if (domainMetricNames == null) {
            domainMetricNames = instantiateDomainMetricNames();
        }
        return domainMetricNames;
    }

    @Override
    public RESPONSE handle(final MESSAGE message) {
        final Context context = message.getContext();
        final MetricNames inputMetricNames = getOrInstantiateInputMetricNames();
        final MetricNames domainMetricNames = getOrInstantiateDomainMetricNames();

        metricRegistry.meter(KasperMetrics.name(MetricNameStyle.CLIENT_TYPE, context, getInputClass(), "requests")).mark();

        final Timer.Context inputTimer = metricRegistry.timer(inputMetricNames.requestsTime).time();
        final Timer.Context domainTimer = metricRegistry.timer(domainMetricNames.requestsTime).time();
        final Timer.Context globalTimer = metricRegistry.timer(globalMetricNames.requestsTime).time();

        RESPONSE response;
        Optional<RuntimeException> exception = Optional.absent();

        try {
            response = handler.handle(message);
        } catch (final ConflictingAggregateVersionException e) {
            response = error(new KasperReason(CoreReasonCode.CONFLICT, e.getMessage()));
        } catch (final RuntimeException e) {
            response = error(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
            exception = Optional.of(e);
        } finally {
            inputTimer.stop();
            domainTimer.stop();
            globalTimer.stop();
        }

        if (isErrorResponse(response)) {
            metricRegistry.meter(inputMetricNames.errors).mark();
            metricRegistry.meter(domainMetricNames.errors).mark();
            metricRegistry.meter(KasperMetrics.name(MetricNameStyle.CLIENT_TYPE, context, getInputClass(), "errors")).mark();
            metricRegistry.meter(globalMetricNames.errors).mark();
        }

        if (exception.isPresent()) {
            logger.error("Handle method throws an Exception in handler " + handler.getClass().getName(), exception.get());
            throw exception.get();
        }

        return response;
    }

    protected boolean isErrorResponse(final KasperResponse response) {
        switch (response.getStatus()) {
            case OK:
            case SUCCESS:
            case ACCEPTED:
            case REFUSED:
                return Boolean.FALSE;

            case ERROR:
            case FAILURE:
            default :
                return Boolean.TRUE;
        }
    }

    @Override
    public Class<INPUT> getInputClass() {
        return handler.getInputClass();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Class<HANDLER> getHandlerClass() {
        return (Class<HANDLER>) handler.getHandlerClass();
    }

    public abstract RESPONSE error(final KasperReason reason);
}