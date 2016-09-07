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

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.AbstractApplicationContext;

public class RabbitMQComponentInjector implements AMQPTopologyListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMQComponentInjector.class);

    private AbstractApplicationContext applicationContext;

    public RabbitMQComponentInjector(AbstractApplicationContext applicationContext) {
        this.applicationContext = Preconditions.checkNotNull(applicationContext);
    }

    protected void register(String name, Object bean) {
        try {
            applicationContext.getBeanFactory().registerSingleton(name, bean);
        } catch (IllegalStateException e) {
            destroy(name);
            applicationContext.getBeanFactory().registerSingleton(name, bean);
            LOGGER.debug("component reference '{}' is replaced in the application context", name);
        }
    }

    protected void destroy(String name) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getBeanFactory();
        beanFactory.destroySingleton(name);
    }

    @Override
    public void onQueueCreated(Queue queue) {
        register(queue.getName(), queue);
        LOGGER.debug("queue '{}' is injected in the application context", queue.getName());
    }

    @Override
    public void onQueueDeleted(String name) {
        destroy(name);
        LOGGER.debug("queue '{}' is deleted from the application context", name);
    }

    @Override
    public void onExchangeCreated(Exchange exchange) {
        register(exchange.getName(), exchange);
        LOGGER.debug("exchange '{}' is injected in the application context", exchange.getName());
    }

    @Override
    public void onExchangeDeleted(String name) {
        destroy(name);
        LOGGER.debug("exchange '{}' is deleted from the application context", name);
    }

    @Override
    public void onBindingCreated(Binding binding) {
        final String name = getBindingNameOf(binding);
        register(name, binding);
        LOGGER.debug("binding '{}' is injected in the application context", name);
    }

    @Override
    public void onBindingDeleted(Binding binding) {
        final String name = getBindingNameOf(binding);
        destroy(name);
        LOGGER.debug("binding '{}' is deleted from the application context", name);
    }

    private String getBindingNameOf(Binding binding) {
        return binding.getRoutingKey() + "_" + binding.getDestination();
    }
}
