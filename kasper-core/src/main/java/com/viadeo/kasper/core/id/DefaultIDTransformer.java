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
    public Map<ID, ID> to(final Format targetFormat, final Collection<ID> ids) {
        checkNotNull(targetFormat);
        checkNotNull(ids);

        checkArgument(Iterables.all(ids, Predicates.notNull()), "Each specified ids must be not null");

        HashMultimap<ImmutablePair<String, Format>, ID> idsByVendorFormat = HashMultimap.create();
        for (ID id : ids) {
            idsByVendorFormat.put(ImmutablePair.of(id.getVendor(), id.getFormat()), id);
        }

        Map<ImmutablePair<String, Format>, Map<ID, ID>> convertedIdsByVendorFormat = Maps.transformEntries(idsByVendorFormat.asMap(), new Maps.EntryTransformer<ImmutablePair<String, Format>, Collection<ID>, Map<ID, ID>>() {
            @Override
            public Map<ID, ID> transformEntry(ImmutablePair<String, Format> key, Collection<ID> values) {

                String vendor = key.first;
                Format sourceFormat = key.second;

                if (values.isEmpty() || targetFormat == sourceFormat) {
                    return Maps.uniqueIndex(values, Functions.<ID>identity());
                }

                for (final Converter converter : converterRegistry.getConverters(vendor, targetFormat)) {
                    if (accept(converter, vendor, sourceFormat, targetFormat)) {
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
        });

        ImmutableMap.Builder<ID, ID> flattened = ImmutableMap.builder();
        for (Map<ID, ID> idMap : convertedIdsByVendorFormat.values()) {
            flattened.putAll(idMap);
        }
        return flattened.build();
    }

    @Override
    public Map<ID, ID> to(final Format format, final ID firstId, final ID... restIds) {
        checkNotNull(format);
        checkNotNull(firstId);
        checkNotNull(restIds);

        return to(format, Lists.asList(firstId, restIds));
    }

    @Override
    public ID to(final Format format, final ID id) {
        checkNotNull(format);
        checkNotNull(id);

        Map<ID, ID> transformedIds = to(format, id, new ID[0]);

        return transformedIds.get(id);
    }

    @Override
    public List<ID> toList(final Format format, final Collection<ID> ids) {
        return newArrayList(to(format, ids).values());
    }

    public boolean accept(final Converter converter, final String vendor, final Format sourceFormat, final Format targetFormat) {
        return converter.getSource() == sourceFormat &&
                converter.getTarget() == targetFormat &&
                converter.getVendor().equals(vendor);
    }

}
