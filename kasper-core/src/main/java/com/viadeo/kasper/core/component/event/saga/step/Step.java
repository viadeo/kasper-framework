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
package com.viadeo.kasper.core.component.event.saga.step;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.event.listener.EventDescriptor;
import com.viadeo.kasper.core.component.event.saga.Saga;

import java.util.List;

/**
 * part of <code>Saga</code>'s lifecycle
 */
public interface Step {

    /**
     * @return the <code>Step</code>'s name
     */
    String name();

    /**
     * invoke the <code>Saga</code>'s <code>Step</code>
     *
     * @param saga the instance of saga
     * @param context the <code>Context</code>
     * @param event the <code>Event</code>
     * @throws StepInvocationException if an error occurs during invocation
     */
    void invoke(Saga saga, Context context, Event event) throws StepInvocationException;

    /**
     * clean all things to be cleaned before storing <code>Saga</code>
     *
     * @param identifier the saga identifier
     */
    void clean(Object identifier);

    /**
     * get the <code>Step</code>'s <code>Event</code> Class
     *
     * @return supported event by this <code>Step</code>
     */
    EventDescriptor getSupportedEvent();

    /**
     * retrieve the <code>Saga</code> identifier from the given <code>Event</code>
     *
     * @param event an <code>Event</code>
     * @return the optional identifier
     */
    Optional<Object> getSagaIdentifierFrom(Event event);

    /**
     * get the <code>Saga</code> Class
     *
     * @return the <code>Saga</code> Class
     */
    Class<? extends Saga> getSagaClass();

    /**
     * get the <code>Step</code> Class
     *
     * @return the <code>Step</code> Class
     */
    Class<? extends Step> getStepClass();

    /**
     * @return a list of identifier accessor
     */
    List<String> getActions();
}
