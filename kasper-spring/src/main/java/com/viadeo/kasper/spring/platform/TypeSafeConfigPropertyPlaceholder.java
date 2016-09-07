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
package com.viadeo.kasper.spring.platform;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import com.typesafe.config.ConfigValue;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TypeSafeConfigPropertyPlaceholder extends PropertySourcesPlaceholderConfigurer {

    private static final Joiner JOINER = Joiner.on(',');

    protected final Config config;

    public TypeSafeConfigPropertyPlaceholder(Config config) {
        this.config = config;
    }

    public TypeSafeConfigPropertyPlaceholder() {
        config = ConfigFactory.load();
    }

    public TypeSafeConfigPropertyPlaceholder(String resource) {
        config = ConfigFactory.load(resource);
    }

    @Override
    @VisibleForTesting
    @SuppressWarnings("unchecked")
    public void loadProperties(Properties props) throws IOException {
        for (final Map.Entry<String, ConfigValue> confEntry : config.entrySet()) {
            final String key = confEntry.getKey();
            final ConfigValue value = confEntry.getValue();

            switch (value.valueType()) {
                case LIST: {
                    try {
                        props.setProperty(key, JOINER.join(((List<String>) value.unwrapped())));
                    } catch (RuntimeException e) {
                        // TODO permits other types of lists?!
                        throw new IllegalArgumentException("listable parameters should only have strings as elements.", e);
                    }
                    break;
                }
                case NULL: {
                    props.setProperty(key, "");
                    break;
                }
                case NUMBER: {
                    props.setProperty(key, value.render());
                    break;
                }
                case STRING: {
                    props.setProperty(key, (String) value.unwrapped());
                    break;
                }
                default: {
                    final String valueRendred = value.render(ConfigRenderOptions.concise().setJson(false));
                    props.setProperty(key, valueRendred);
                    break;
                }
            }
        }
    }

}