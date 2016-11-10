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
package com.viadeo.kasper.exposition.http;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.response.CoreReasonCode;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static javax.ws.rs.core.Response.Status;

public final class CoreReasonHttpCodes {

    private static final Map<String, CoreReasonHttpCodesTranslationTable> STRING_CODES = Maps.newHashMap();

    private CoreReasonHttpCodes() { /* Utility class */ }

    // ------------------------------------------------------------------------

    private enum CoreReasonHttpCodesTranslationTable {
        UNKNOWN_REASON(CoreReasonCode.UNKNOWN_REASON, Status.INTERNAL_SERVER_ERROR),

        REQUIRED_INPUT(CoreReasonCode.REQUIRED_INPUT, Status.BAD_REQUEST),
        INVALID_INPUT(CoreReasonCode.INVALID_INPUT, Status.BAD_REQUEST),
        TOO_MANY_ENTRIES(CoreReasonCode.TOO_MANY_ENTRIES, Status.BAD_REQUEST),
        CONFLICT(CoreReasonCode.CONFLICT, Status.CONFLICT),
        INVALID_ID(CoreReasonCode.INVALID_ID, Status.BAD_REQUEST),
        NOT_FOUND(CoreReasonCode.NOT_FOUND, Status.NOT_FOUND),
        UNSUPPORTED_MEDIA_TYPE(CoreReasonCode.UNSUPPORTED_MEDIA_TYPE, Status.UNSUPPORTED_MEDIA_TYPE),

        REQUIRE_AUTHENTICATION(CoreReasonCode.REQUIRE_AUTHENTICATION, Status.UNAUTHORIZED),
        REQUIRE_AUTHORIZATION(CoreReasonCode.REQUIRE_AUTHORIZATION, Status.FORBIDDEN),
        INVALID_AUTHENTICATION(CoreReasonCode.INVALID_AUTHENTICATION, Status.BAD_REQUEST),
        INVALID_SECURITY_TOKEN(CoreReasonCode.INVALID_SECURITY_TOKEN, Status.BAD_REQUEST),

        INVALID_CREDENTIALS(CoreReasonCode.INVALID_CREDENTIALS, Status.UNAUTHORIZED),
        INVALID_USER(CoreReasonCode.INVALID_USER, Status.UNAUTHORIZED),
        LOCKED_USER(CoreReasonCode.LOCKED_USER, Status.UNAUTHORIZED),
        DELETED_USER(CoreReasonCode.DELETED_USER, Status.UNAUTHORIZED),

        INTERNAL_COMPONENT_TIMEOUT(CoreReasonCode.INTERNAL_COMPONENT_TIMEOUT, Status.INTERNAL_SERVER_ERROR),
        INTERNAL_COMPONENT_ERROR(CoreReasonCode.INTERNAL_COMPONENT_ERROR, Status.INTERNAL_SERVER_ERROR),
        SERVICE_UNAVAILABLE(CoreReasonCode.SERVICE_UNAVAILABLE, Status.SERVICE_UNAVAILABLE)
        ;

        //---------------------------------------------------------------------


        private final CoreReasonCode code;
        private final Status httpStatus;

        // --------------------------------------------------------------------

        CoreReasonHttpCodesTranslationTable(final CoreReasonCode code, final Status httpStatus) {
            this.code = code;
            this.httpStatus = httpStatus;
            STRING_CODES.put(code.name(), this);
        }

        // --------------------------------------------------------------------

        public Status getHttpStatus() {
            return this.httpStatus;
        }

        public CoreReasonCode getCode() {
            return this.code;
        }

        public int getStatusCode() {
            return this.httpStatus.getStatusCode();
        }

        public static CoreReasonHttpCodesTranslationTable fromString(final String stringCode) {
           if (STRING_CODES.containsKey(stringCode)) {
               return STRING_CODES.get(stringCode);
           }
           throw new IllegalArgumentException(stringCode + " is not a valid code code");
        }

    }

    // ------------------------------------------------------------------------

    public static int toStatus(final String code) {
        try {
            final CoreReasonHttpCodesTranslationTable mapping = CoreReasonHttpCodesTranslationTable.fromString(checkNotNull(code));
            return mapping.getStatusCode();
        } catch (final IllegalArgumentException e) {
            return Status.INTERNAL_SERVER_ERROR.getStatusCode();
        }
    }

}
