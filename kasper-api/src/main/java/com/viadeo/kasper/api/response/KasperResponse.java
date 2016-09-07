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
package com.viadeo.kasper.api.response;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.Immutable;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperResponse implements Serializable, Immutable {

    /**
     * Accepted values for command response statuses
     */
    public enum Status {
        OK,         /** All is ok */
        REFUSED,    /** Refused by some intermediate validation mechanisms */
        ERROR,      /** Error in handling or domain business */
        ACCEPTED,   /** The command or the query has been accepted but answer will be made later */
        FAILURE,
        SUCCESS
    }

    /**
     * The current command status
     */
    private final Status status;
    private final KasperReason reason;

    // ------------------------------------------------------------------------

    public KasperResponse() {
        this(Status.OK, null);
    }

    public KasperResponse(final KasperResponse response) {
        this(checkNotNull(response).status, response.reason);
    }

    public KasperResponse(final Status status, final KasperReason reason) {
        this.status = checkNotNull(status);

        if ( ! Lists.newArrayList(Status.OK, Status.SUCCESS).contains(status) && (null == reason)) {
            throw new IllegalStateException("Please provide a reason to the response");
        }

        if (Lists.newArrayList(Status.OK, Status.SUCCESS).contains(status) && (null != reason)) {
            throw new IllegalStateException("Invalid response OK provided with an reason");
        }

        this.reason = reason;
    }

    // ------------------------------------------------------------------------

    /**
     * @return the current command response execution status
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * @return true if the current status is OK
     */
    public boolean isOK() {
        return (this.status.equals(Status.OK));
    }

    /**
     * @return a list of reasons or empty if command succeeded.
     */
    public KasperReason getReason() {
        return reason;
    }

    /**
     * @return true if this command has answered with a reason
     */
    public boolean hasReason() {
        return Optional.fromNullable(getReason()).isPresent();
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final KasperResponse other = (KasperResponse) obj;

        return com.google.common.base.Objects.equal(this.status, other.status)
                && com.google.common.base.Objects.equal(this.reason, other.reason);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(status, reason);
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .addValue(this.status)
                .addValue(this.reason)
                .toString();
    }

}
