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
package com.viadeo.kasper.api.component.event;

import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.api.response.KasperResponse;

public class EventResponse extends KasperResponse {

    /**
     * Get a temporarily unavailable response which is identified as a particular failure.
     *
     * @param reason a reason
     * @return a temporarily unavailable response
     */
    public static EventResponse temporarilyUnavailable(final KasperReason reason) {
        return failure(reason).temporary();
    }

    /**
     * Get an error response which is an expected part of normal operations, are dealt with immediately and the system
     * will continue to operate at the same capacity following an error. For example, an error discovered
     * during input validation that will be communicated to the client as part of normal processing.
     *
     * @param reason a reason
     * @return an error response
     */
    public static EventResponse error(final KasperReason reason) {
        return new EventResponse(Status.ERROR, reason);
    }

    /**
     * Get a failure response which is an unexpected and can require intervention before the system can resume at the
     * same level of operation. This does not mean that failures are always fatal, rather that some capacity of the
     * system will be reduced following a failure.
     *
     * @param reason a reason
     * @return a failure response
     */
    public static EventResponse failure(final KasperReason reason) {
        return new EventResponse(Status.FAILURE, reason);
    }

    /**
     * Get a success response.
     *
     * @return a success response
     */
    public static EventResponse success() {
        return new EventResponse(Status.SUCCESS, null);
    }

    /**
     * @return an ignore response
     * @deprecated prefer to use one of other kind of response
     */
    @Deprecated
    public static EventResponse ignored() {
        return new EventResponse(Status.OK, null);
    }

    private boolean temporary;

    public EventResponse(final Status status, final KasperReason reason) {
        super(status, reason);
    }

    @Override
    public boolean isOK() {
        return getStatus().equals(Status.SUCCESS) || super.isOK();
    }

    public boolean isAnError() {
        return getStatus() == Status.ERROR;
    }

    public boolean isAFailure() {
        return getStatus() == Status.FAILURE;
    }

    private EventResponse temporary() {
        temporary = true;
        return this;
    }

    public boolean isTemporary() {
        return temporary;
    }
}
