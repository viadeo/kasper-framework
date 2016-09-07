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
package com.viadeo.kasper.common.exposition;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.context.Context;

import static com.google.common.base.Preconditions.checkNotNull;

public enum HttpContextHeaders {
    HEADER_SESSION_CORRELATION_ID("X-KASPER-SESSION-CID", Context.SESSION_CID_SHORTNAME),
    HEADER_FUNNEL_CORRELATION_ID("X-KASPER-FUNNEL-CID", Context.FUNNEL_CID_SHORTNAME),
    HEADER_FUNNEL_NAME("X-KASPER-FUNNEL-NAME", Context.FUNNEL_NAME_SHORTNAME),
    HEADER_FUNNEL_VERSION("X-KASPER-FUNNEL-VERSION", Context.FUNNEL_VERS_SHORTNAME),
    HEADER_REQUEST_CORRELATION_ID("X-KASPER-REQUEST-CID", Context.REQUEST_CID_SHORTNAME),
    HEADER_USER_ID("X-KASPER-UID", Context.UID_SHORTNAME),
    HEADER_USER_LANG("X-KASPER-LANG", Context.ULANG_SHORTNAME),
    HEADER_USER_COUNTRY("X-KASPER-COUNTRY", Context.UCOUNTRY_SHORTNAME),
    HEADER_SECURITY_TOKEN("X-KASPER-SECURITY-TOKEN", Context.SECURITY_TOKEN_SHORTNAME),
    HEADER_ACCESS_TOKEN("X-KASPER-ACCESS-TOKEN", Context.ACCESS_TOKEN_SHORTNAME),
    HEADER_AUTHENTICATION_TOKEN("X-KASPER-AUTHENTICATION-TOKEN", Context.AUTHENTICATION_TOKEN_SHORTNAME),
    HEADER_REQUEST_IP_ADDRESS("X-Forwarded-For", Context.IP_ADDRESS_SHORTNAME),
    HEADER_TAGS("X-KASPER-TAGS", Context.TAGS_SHORTNAME),
    HEADER_CALL_TYPE("X-KASPER-CALL-TYPE", Context.CALL_TYPE),
    HEADER_USER_AGENT("User-Agent", Context.USER_AGENT),
    HEADER_REFERER("Referer", Context.REFERER),
    HEADER_SERVER_NAME("X-KASPER-SERVER-NAME", Context.SERVER_NAME),
    HEADER_APPLICATION_ID("X-KASPER-CLIENT-APPID", Context.APPLICATION_ID_SHORTNAME),
    HEADER_APPLICATION_VERSION("X-KASPER-CLIENT-APPVERSION", Context.APPLICATION_VERSION_SHORTNAME),
    HEADER_APPLICATION_PLATFORM("X-KASPER-CLIENT-APPPLATFORM", Context.APPLICATION_PLATFORM_SHORTNAME),
    HEADER_CLIENT_ID("X-KASPER-CLIENT-ID", Context.CLIENT_ID_SHORTNAME),
    HEADER_CLIENT_VERSION("X-KASPER-CLIENT-VERSION", Context.CLIENT_VERSION_SHORTNAME),
    HEADER_KASPER_ID("X-KASPER-CID", Context.KASPER_CID_SHORTNAME)
    ;

    private final String headerName;
    private final String propertyKey;

    HttpContextHeaders(String headerName, String propertyKey) {
        this.headerName = checkNotNull(headerName);
        this.propertyKey = checkNotNull(propertyKey);
    }

    public String toPropertyKey() {
        return propertyKey;
    }

    public String toHeaderName() {
        return headerName;
    }

    public static Optional<HttpContextHeaders> fromHeader(final String headerName) {
        checkNotNull(headerName);
        for (HttpContextHeaders httpContextHeader : HttpContextHeaders.values()) {
            if (headerName.equalsIgnoreCase(httpContextHeader.headerName)) {
                return Optional.of(httpContextHeader);
            }
        }
        return Optional.absent();
    }

    public static Optional<HttpContextHeaders> fromPropertyKey(final String propertyKey) {
        checkNotNull(propertyKey);
        for (HttpContextHeaders httpContextHeader : HttpContextHeaders.values()) {
            if (propertyKey.equalsIgnoreCase(httpContextHeader.propertyKey)) {
                return Optional.of(httpContextHeader);
            }
        }
        return Optional.absent();
    }

    @Override
    public String toString() {
        return headerName;
    }
}
