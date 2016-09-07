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
package com.viadeo.kasper.platform.configuration;

import com.codahale.metrics.MetricRegistry;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.platform.ExtraComponent;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptorFactory;

import java.util.List;

/**
 * The PlatformConfiguration interface provides methods to define base components. These components are required in order
 * to initialize a new {@link com.viadeo.kasper.platform.Platform}
 */
public interface PlatformConfiguration {

    /**
     * @return the event bus
     */
    KasperEventBus eventBus();

    /**
     * @return the command gateway
     */
    KasperCommandGateway commandGateway();

    /**
     * @return the query gateway
     */
    KasperQueryGateway queryGateway();

    /**
     * @return the metric registry
     */
    MetricRegistry metricRegistry();

    /**
     * @return the saga manager
     */
    SagaManager sagaManager();

    /**
     * @return the configuration
     */
    Config configuration();

    /**
     * @return the extra components
     */
    List<ExtraComponent> extraComponents();

    /**
     * @return the list of interceptor factories dedicated to the command side
     */
    List<CommandInterceptorFactory> commandInterceptorFactories();

    /**
     * @return the list of interceptor factories dedicated to the query side
     */
    List<QueryInterceptorFactory> queryInterceptorFactories();

    /**
     * @return the list of interceptor factories dedicated to the event side
     */
    List<EventInterceptorFactory> eventInterceptorFactories();

    /**
     * @return the domain descriptor factory
     */
    DomainDescriptorFactory domainDescriptorFactory();

}
