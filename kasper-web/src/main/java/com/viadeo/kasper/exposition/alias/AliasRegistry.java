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
        String name = nameByAlias.get(checkNotNull(alias));
        return name == null ? alias : name;
    }

    public Optional<List<String>> aliasesOf(final String name) {
        return Optional.fromNullable(aliasesByName.get(checkNotNull(name)));
    }

}
