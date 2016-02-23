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

        Map<ImmutablePair<String, Format>, Map<ID, ID>> convertedIdsByVendorFormat = Maps.transformEntries(idsByVendorFormat.asMap(), new Maps.EntryTransformer<ImmutablePair<String, Format>, Collection<ID>, Map<ID, ID>>() {
            @Override
            public Map<ID, ID> transformEntry(ImmutablePair<String, Format> key, Collection<ID> values) {
                return doConvert(key.first, key.second, targetFormat, values);
            }
        });

        ImmutableMap.Builder<ID, ID> flattened = ImmutableMap.builder();
        for (Map<ID, ID> idMap : convertedIdsByVendorFormat.values()) {
            flattened.putAll(idMap);
        }
        return flattened.build();
    }

    private Map<ID, ID> doConvert(String vendor, Format sourceFormat, Format targetFormat, Collection<ID> values) {
        if (values.isEmpty() || targetFormat == sourceFormat) {
            return Maps.uniqueIndex(values, Functions.<ID>identity());
        }

        for (final Converter converter : converterRegistry.getConverters(vendor, targetFormat)) {
            if (converter.getSource() == sourceFormat &&
                    converter.getTarget() == targetFormat &&
                    converter.getVendor().equals(vendor)) {
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
    public List<ID> toList(final Format format, final Collection<ID> ids) {
        return newArrayList(to(format, ids).values());
    }

}
