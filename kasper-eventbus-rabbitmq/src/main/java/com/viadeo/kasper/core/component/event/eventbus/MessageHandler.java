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
package com.viadeo.kasper.core.component.event.eventbus;

import com.codahale.metrics.MetricRegistry;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class MessageHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageHandler.class);

    public static final String HANDLE_MESSAGE_COUNT_METRIC = KasperMetrics.name(MessageHandler.class, "handle-message", "count");
    public static final String HANDLE_MESSAGE_ERROR_METRIC = KasperMetrics.name(MessageHandler.class, "handle-message", "error");
    public static final String HANDLE_MESSAGE_TIME_METRIC = KasperMetrics.name(MessageHandler.class, "handle-message", "time");

    private final EventListener eventListener;
    private final MetricRegistry metricRegistry;
    private final boolean enabledMessageHandling;

    public MessageHandler(EventListener eventListener, MetricRegistry metricRegistry, boolean enabledMessageHandling) {
        this.eventListener = eventListener;
        this.metricRegistry = metricRegistry;
        this.enabledMessageHandling = enabledMessageHandling;
    }

    @SuppressWarnings("unused")
    public void handleMessage(EventMessage eventMessage) {
        metricRegistry.counter(HANDLE_MESSAGE_COUNT_METRIC).inc();
        metricRegistry.histogram(HANDLE_MESSAGE_TIME_METRIC).update(timeTaken(eventMessage));

        MDC.setContextMap(
                Maps.transformEntries(
                        eventMessage.getMetaData(),
                        new Maps.EntryTransformer<String, Object, String>() {
                            @Override
                            public String transformEntry( String key, Object value) {
                                return String.valueOf(value);
                            }
                        }
                )
        );

        try {
            if (enabledMessageHandling) {
                eventListener.handle(eventMessage);
            }
        } catch (Exception t) {
            metricRegistry.counter(HANDLE_MESSAGE_ERROR_METRIC).inc();
            LOGGER.warn("failed to handle event message by '{}'", eventListener.getClass().getName(), t);

            throw new MessageHandlerException(eventListener.getClass(), t);
        }
    }

    private long timeTaken(EventMessage eventMessage) {
        return System.currentTimeMillis() - eventMessage.getTimestamp().getMillis();
    }
}
