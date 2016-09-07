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
package com.viadeo.kasper.domain.sample.hello.api.event;

import com.viadeo.kasper.api.annotation.XKasperEvent;
import com.viadeo.kasper.api.component.event.EntityUpdatedEvent;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.domain.sample.hello.api.HelloDomain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * This is an event concerning directly an entity change, let's mark it with EntityUpdatedEvent interface
 *
 * It's an HelloEvent
 *
 */
@XKasperEvent(
        description = "The recipient buddy of an existing hello message changed",
        action = "changed"
)
public class BuddyChangedForHelloMessageEvent
        extends EntityUpdatedEvent<HelloDomain>
        implements HelloEvent {

    private final KasperID entityId;
    private final String originalForBuddy;
    private final String newForBuddy;

    // ------------------------------------------------------------------------

    public BuddyChangedForHelloMessageEvent(
            final KasperID entityId,
            final String originalForBuddy,
            final String newForBuddy)
    {
        this.entityId = checkNotNull(entityId);
        this.originalForBuddy = checkNotNull(originalForBuddy);
        this.newForBuddy = checkNotNull(newForBuddy);
    }

    // ------------------------------------------------------------------------

    public String getOriginalForBuddy() {
        return this.originalForBuddy;
    }

    public String getNewForBuddy() {
        return this.newForBuddy;
    }

    public KasperID getEntityId() {
        return entityId;
    }
}
