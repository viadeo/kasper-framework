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
package com.viadeo.kasper.api.id;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimpleIDBuilder implements IDBuilder {

    private static final Pattern URN_PATTERN = Pattern.compile("^urn:([a-zA-Z]+):([a-zA-Z\\-]+):([a-zA-Z\\-]+):(.+)$");

    public final Map<String, Format> formatByName;

    public SimpleIDBuilder(final Format... formats) {
        this.formatByName = Maps.newHashMap();

        for (final Format format : formats) {
            formatByName.put(format.name(), format);
        }
    }
    @Override
    public ID build(final String urn) {
        Preconditions.checkNotNull(urn);

        final Matcher matcher = URN_PATTERN.matcher(urn);

        if ( ! matcher.find()) {
            throw new IllegalArgumentException(
                    String.format("Invalid URN layout, <URN=%s>", urn)
            );
        }

        final String formatAsString = checkNotNull(matcher.group(3));

        final Format format = formatByName.get(formatAsString);

        if (format == null) {
            throw new IllegalArgumentException(
                    String.format("Invalid URN format, format not supported '%s'", formatAsString)
            );
        }

        return new ID(
                checkNotNull(matcher.group(1)),
                checkNotNull(matcher.group(2)),
                format,
                format.parseIdentifier(checkNotNull(matcher.group(4)))
        );
    }

    @Override
    public Collection<Format> getSupportedFormats() {
        return Lists.newArrayList(formatByName.values());
    }
}
