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
package com.viadeo.kasper.spring.core;

import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.api.id.IDTransformer;
import com.viadeo.kasper.api.id.TransformableIDBuilder;
import com.viadeo.kasper.core.id.Converter;
import com.viadeo.kasper.core.id.ConverterRegistry;
import com.viadeo.kasper.core.id.DefaultIDTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class KasperIDConfiguration {

    @Autowired(required = false)
    List<Format> formats;

    @Autowired(required = false)
    List<Converter> converters;

    @Bean
    public ConverterRegistry idConverterRegistry() {
        ConverterRegistry converterRegistry = new ConverterRegistry();
        if (converters != null) {
            for (Converter converter : converters) {
                converterRegistry.register(converter);
            }
        }
        return converterRegistry;
    }

    @Bean
    public IDTransformer idTransformer(ConverterRegistry converterRegistry) {
        return new DefaultIDTransformer(converterRegistry);
    }

    @Bean
    public IDBuilder idBuilder(IDTransformer idTransformer) {
        return new TransformableIDBuilder(
                idTransformer,
                formats != null ? formats.toArray(new Format[formats.size()]) : new Format[] {}
        );
    }
}
