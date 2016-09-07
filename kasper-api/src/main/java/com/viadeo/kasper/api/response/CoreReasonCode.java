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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public enum CoreReasonCode {

    /* Unknown reason */
    UNKNOWN_REASON(0),

    /* Input reasons */
    REQUIRED_INPUT(1001),
    INVALID_INPUT(1002),
    TOO_MANY_ENTRIES(1003),
    CONFLICT(1004),
    INVALID_ID(1005),
    NOT_FOUND(1006),
    UNSUPPORTED_MEDIA_TYPE(1007),

    /* Security reasons */
    REQUIRE_AUTHENTICATION(2001),
    REQUIRE_AUTHORIZATION(2002),
    INVALID_AUTHENTICATION(2003),
    INVALID_CREDENTIALS(2004),
    INVALID_USER(2005),
    LOCKED_USER(2006),
    DELETED_USER(2007),
    INVALID_SECURITY_TOKEN(2008),

    /* Internal reasons */
    INTERNAL_COMPONENT_TIMEOUT(3001),
    INTERNAL_COMPONENT_ERROR(3002),
    SERVICE_UNAVAILABLE(3003);


    private static final String CODE_FORMAT = "%04d";

    // ------------------------------------------------------------------------

    private int code;
    private KasperReason reason;

    // ------------------------------------------------------------------------

    CoreReasonCode(final int code) {
        checkState(code < 10000, "Code must be high than 9999");
        this.code = code;
        this.reason = new KasperReason(String.valueOf(this.code), this.name());
    }

    // ------------------------------------------------------------------------

    public KasperReason reason() {
        return this.reason;
    }

    public KasperReason.Builder builder() {
        return KasperReason.Builder.from(reason);
    }

    public String string() {
        return this.toString();
    }

    public int code() {
        return this.code;
    }

    // ------------------------------------------------------------------------

    public static final class ParsedCode {
        public int code;
        public String label;
        public CoreReasonCode reason;
    }

    public static ParsedCode parseString(final String string) {
        final ParsedCode ret = new ParsedCode();

        final Pattern codePattern = Pattern.compile("^.([0-9]+). - (.*)$");
        final Matcher m = codePattern.matcher(string);
        if (m.matches() && (2 == m.groupCount())) {
            final String strCode = m.group(1);
            final String strLabel = m.group(2);
            try {
                ret.reason = CoreReasonCode.valueOf(strLabel);
                ret.code = ret.reason.code();
                ret.label = ret.reason.name();
            } catch (final IllegalArgumentException e) {
                ret.code = Integer.parseInt(strCode);
                ret.label = strLabel;
            }

        } else {
            ret.code = 0;
            ret.label = string;
            try {
                ret.reason = CoreReasonCode.valueOf(string);
                ret.code = ret.reason.code();
                ret.label = ret.reason.name();
            } catch (final IllegalArgumentException e) {
                ret.code = 0;
                ret.label = string;
            }
        }

        return ret;
    }

    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return toString(this.code, this.name());
    }

    public static String toString(final Integer code, final String label) {
         return new StringBuffer()
                    .append(String.format("["+CODE_FORMAT+"]", code))
                    .append(" - ")
                    .append(label)
                .toString();
    }

    public boolean equals(final String other) {
        return this.toString().contentEquals(other);
    }

}
