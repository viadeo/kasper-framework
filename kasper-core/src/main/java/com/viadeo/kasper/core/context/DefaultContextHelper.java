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
package com.viadeo.kasper.core.context;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.ContextHelper;
import com.viadeo.kasper.api.context.Version;
import com.viadeo.kasper.api.id.IDBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultContextHelper implements ContextHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContextHelper.class);
    private static final Version NONE = new Version() {
        @Override
        public Context apply(Context context) {
            return context;
        }
    };

    private final IDBuilder idBuilder;
    private final Version version;

    public DefaultContextHelper(final IDBuilder idBuilder) {
        this(NONE, idBuilder);
    }

    public DefaultContextHelper(final Version version, final IDBuilder idBuilder) {
        this.version = checkNotNull(version);
        this.idBuilder = checkNotNull(idBuilder);
    }

    @Override
    public Context createFrom(Map<String, String> contextAsMap) {
        Map<String,String> contextCopy = Maps.newHashMap(contextAsMap);
        String urn = contextCopy.remove(Context.USER_ID_SHORTNAME);
        String kasperCorrelationUuid = contextCopy.get(Context.KASPER_CID_SHORTNAME);
        String sequence = contextCopy.get(Context.SEQ_INC_SHORTNAME);

        Context.Builder builder = new Context.Builder(
                kasperCorrelationUuid == null ? UUID.randomUUID() : UUID.fromString(kasperCorrelationUuid),
                sequence == null ? 1 : Integer.parseInt(sequence)
        );

        if (urn != null && !urn.trim().isEmpty()) {
            try {
                builder.withUserID(idBuilder.build(urn));
            } catch (IllegalArgumentException e) {
                LOGGER.warn("Failed to build ID", e);
            }
        }

        for (Map.Entry<String, String> entry : contextCopy.entrySet()) {
            builder.with(entry.getKey(), entry.getValue());
        }

        return version.apply(builder.build());
    }
}
