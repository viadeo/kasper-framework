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
package com.viadeo.kasper.api.component.event;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.response.CoreReasonCode;

import static com.google.common.base.Preconditions.checkNotNull;

public class ErrorEvent implements Event {

    private final String code;
    private final Exception exception;
    private final String message;

    // ------------------------------------------------------------------------

    public ErrorEvent(final String code,
                      final String message,
                      final Exception exception) {
        this.code = checkNotNull(code);
        this.exception = checkNotNull(exception);
        this.message = checkNotNull(message);
    }

    public ErrorEvent(final String code, final Exception exception) {
        this.code = checkNotNull(code);
        this.exception = checkNotNull(exception);
        this.message = null;
    }

    public ErrorEvent(final String code, final String message) {
        this.code = checkNotNull(code);
        this.exception = null;
        this.message = checkNotNull(message);
    }

    public ErrorEvent(final String message) {
        this.code = CoreReasonCode.UNKNOWN_REASON.toString();
        this.exception = null;
        this.message = checkNotNull(message);
    }

    public ErrorEvent(final Exception exception) {
        this.code = CoreReasonCode.UNKNOWN_REASON.toString();
        this.exception = checkNotNull(exception);
        this.message = null;
    }

    public ErrorEvent(final CoreReasonCode code,
                      final String message, final Exception exception) {
         this(code.toString(), message, exception);
    }

    public ErrorEvent(final CoreReasonCode code, final Exception exception) {
        this(code.toString(), exception);
    }

    public ErrorEvent(final CoreReasonCode code, final String message) {
        this(code.toString(), message);
    }

    // ------------------------------------------------------------------------

    public Optional<Exception> getException() {
        return Optional.fromNullable(this.exception);
    }

    public Optional<String> getMessage() {
        if (null != this.message) {
            return Optional.of(this.message);
        }
        if (null != this.exception) {
            return Optional.fromNullable(this.exception.getMessage());
        }
        return Optional.absent();
    }

    public Optional<String> getCode() {
        return Optional.fromNullable(this.code);
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return Objects.hashCode(super.hashCode(), this.code, this.exception, this.message);
    }

    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == checkNotNull(obj)) {
            return true;
        }
        if ( ! getClass().equals(obj.getClass())) {
            return false;
        }

        final ErrorEvent other = (ErrorEvent) obj;

        return  Objects.equal(this.code, other.code) &&
                Objects.equal(this.exception, other.exception) &&
                Objects.equal(this.message, other.message);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(super.toString())
                .addValue(this.code)
                .addValue(this.exception)
                .addValue(this.message)
                .toString();
    }

}
