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
package com.viadeo.kasper.core.id;

import com.google.common.base.Functions;
import com.google.common.base.Predicates;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.IDTransformer;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public class DefaultIDTransformer implements IDTransformer {

    private final ConverterRegistry converterRegistry;

    public DefaultIDTransformer(ConverterRegistry converterRegistry) {
        this.converterRegistry = checkNotNull(converterRegistry);
    }

    @Override
    public Map<ID, ID> to(final Format format, final Collection<ID> ids) {
        checkNotNull(format);
        checkNotNull(ids);
        checkArgument(Iterables.all(ids, Predicates.notNull()), "Each specified ids must be not null");

        return doConvertAll(format, ids);
    }

    @Override
    public Map<ID, ID> to(final Format format, final ID firstId, final ID... restIds) {
        checkNotNull(format);
        List<ID> ids = Lists.asList(firstId, restIds);
        checkArgument(Iterables.all(ids, Predicates.notNull()), "Each specified ids must be not null");

        return doConvertAll(format, ids);
    }

    @Override
    public ID to(final Format format, final ID id) {
        checkNotNull(format);
        checkNotNull(id);

        Map<ID, ID> transformedIds = doConvert(id.getVendor(), id.getFormat(), format, Collections.singletonList(id));

        return transformedIds.get(id);
    }

    private Map<ID, ID> doConvertAll(final Format targetFormat, Collection<ID> ids) {
        HashMultimap<ImmutablePair<String, Format>, ID> idsByVendorFormat = HashMultimap.create();
        for (ID id : ids) {
            idsByVendorFormat.put(ImmutablePair.of(id.getVendor(), id.getFormat()), id);
        }

        ImmutableMap.Builder<ID, ID> convertedIds = ImmutableMap.builder();

        for (Map.Entry<ImmutablePair<String, Format>, Collection<ID>> entry : idsByVendorFormat.asMap().entrySet()) {
            ImmutablePair<String, Format> key = entry.getKey();
            Collection<ID> values = entry.getValue();

            convertedIds.putAll(doConvert(key.first, key.second, targetFormat, values));
        }

        return convertedIds.build();
    }

    private Map<ID, ID> doConvert(String vendor, Format sourceFormat, Format targetFormat, Collection<ID> values) {
        if (values.isEmpty() || targetFormat.equals(sourceFormat)) {
            return Maps.uniqueIndex(values, Functions.<ID>identity());
        }

        for (final Converter converter : converterRegistry.getConverters(vendor, targetFormat)) {
            if (sourceFormat.equals(converter.getSource()) &&
                    targetFormat.equals(converter.getTarget()) &&
                    vendor.equals(converter.getVendor())) {
                try {
                    return converter.convert(values);
                } catch (FailedToTransformIDException e) {
                    throw e;
                } catch (RuntimeException e) {
                    throw new FailedToTransformIDException(
                            String.format("Failed to convert id from '%s' to '%s', <ids=%s>", sourceFormat, targetFormat, values), e
                    );
                }
            }
        }

        throw new FailedToTransformIDException(
                String.format("No available converter allowing to convert id from '%s' to '%s', <ids=%s>", sourceFormat, targetFormat, values)
        );
    }

    @Override
    @Deprecated
    public List<ID> toList(final Format format, final Collection<ID> ids) {
        return newArrayList(to(format, ids).values());
    }

}
