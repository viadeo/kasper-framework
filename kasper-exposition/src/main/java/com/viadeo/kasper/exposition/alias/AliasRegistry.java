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
package com.viadeo.kasper.exposition.alias;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.annotation.XKasperAlias;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;

import java.beans.Introspector;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class AliasRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(AliasRegistry.class);

    public static List<String> aliasesFrom(final Class clazz) {
        final List<String> aliases = Lists.newArrayList();
        @SuppressWarnings("unchecked")
        final XKasperAlias XKasperAliasAnnotation = (XKasperAlias) clazz.getAnnotation(XKasperAlias.class);

        if (null != XKasperAliasAnnotation) {
            for (final String alias : XKasperAliasAnnotation.values()) {
                aliases.add(Introspector.decapitalize(checkNotNull(alias)));
            }
        }

        return aliases;
    }

    private final MultiValueMap<String, String> aliasesByName;
    private final Map<String, String> nameByAlias;

    // ------------------------------------------------------------------------

    public AliasRegistry() {
        this.aliasesByName = CollectionUtils.toMultiValueMap(Maps.<String, List<String>>newHashMap());
        this.nameByAlias = Maps.newHashMap();
    }

    // ------------------------------------------------------------------------

    public void register(final String name, final List<String> aliases) {
        checkNotNull(name);
        checkNotNull(aliases);

        if (null != aliasesByName.get(name)) {
            final String error = String.format(
                    "Unable to register aliases : Some aliases are already registered for the name `%s`",
                    name
            );
            LOGGER.error(error);
            throw new RuntimeException(error);
        }

        aliasesByName.put(name, aliases);

        for (final String alias : aliases) {
            final String existingName = nameByAlias.get(alias);

            if (null != existingName) {
                final String error = String.format(
                        "Unable to register an alias already used : `%s` used by `%s`",
                        alias, existingName
                );
                LOGGER.error(error);
                throw new RuntimeException(error);
            }

            nameByAlias.put(alias, name);
        }
    }

    public String resolve(final String alias) {
        final String name = nameByAlias.get(checkNotNull(alias));
        return (null == name) ? alias : name;
    }

    public Optional<List<String>> aliasesOf(final String name) {
        return Optional.fromNullable(aliasesByName.get(checkNotNull(name)));
    }

}
