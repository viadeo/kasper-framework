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
import com.viadeo.kasper.api.component.event.ErrorEvent;
import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.response.CoreReasonCode;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * It's an error event, lets clearly mark this quality implementing ErrorEvent
 *
 * It's an HelloDomainEvent
 *
 */
@XKasperEvent(
        description = "The recipient of an hello message responded to it",
        action = "responded"
)
public class BuddyWasUnableToRespondErrorEvent
        extends ErrorEvent
        implements HelloDomainEvent {

    private final KasperID fromHello;
    private final String response;

    // ------------------------------------------------------------------------

    public BuddyWasUnableToRespondErrorEvent(
            final KasperID fromHello,
            final String response) {
        super(CoreReasonCode.INTERNAL_COMPONENT_ERROR, response);
        this.fromHello = checkNotNull(fromHello);
        this.response = checkNotNull(response);
    }

    // ------------------------------------------------------------------------

    public KasperID getFromHello() {
        return this.fromHello;
    }

    public String getResponse() {
        return this.response;
    }

}
