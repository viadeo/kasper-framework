// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id;

import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.IDTransformer;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;

public class DefaultIDTransformer implements IDTransformer {

    private static final Function<ID, String> GET_VENDOR_FUNCTION = new Function<ID, String>() {
        @Override
        public java.lang.String apply(ID id) {
            return checkNotNull(id).getVendor();
        }
    };

    private static final Function<ID, Format> GET_FORMAT_FUNCTION = new Function<ID, Format>() {
        @Override
        public Format apply(ID id) {
            return checkNotNull(id).getFormat();
        }
    };

    private final KasperIDsConverterRegistry kasperIDsConverterRegistry;

    public DefaultIDTransformer(KasperIDsConverterRegistry kasperIDsConverterRegistry) {
        this.kasperIDsConverterRegistry = checkNotNull(kasperIDsConverterRegistry);
    }

    @Override
    public Map<ID,ID> to(final Format format, final Collection<ID> givenIds) {
        checkNotNull(format);
        checkNotNull(givenIds);

        final Set<ID> ids = Sets.newHashSet(givenIds);

        checkArgument(Iterables.all(ids, Predicates.notNull()), "Each specified ids must be not null");
        checkArgument(hasSameValue(ids, GET_FORMAT_FUNCTION), "Each specified ids must have the same format");
        checkArgument(hasSameValue(ids, GET_VENDOR_FUNCTION), "Each specified ids must have the same vendor");

        if (ids.isEmpty()) {
            return doNothing(ids);
        }

        final ID firstElement = ids.iterator().next();

        if (format == ids.iterator().next().getFormat()) {
            return doNothing(ids);
        }

        final Format currentFormat = firstElement.getFormat();
        final String currentVendor = firstElement.getVendor();

        final Collection<Converter> converters = kasperIDsConverterRegistry.getConverters(currentVendor, format);

        for (final Converter converter : converters) {
            if (accept(converter, currentVendor, currentFormat, format)) {
                try {
                    return converter.convert(ids);
                } catch (FailedToTransformIDException t) {
                    throw t;
                } catch (RuntimeException e) {
                    throw new FailedToTransformIDException(
                            String.format("Failed to convert id from '%s' to '%s', <ids=%s>", currentFormat, format, ids), e
                    );
                }
            }
        }

        throw new FailedToTransformIDException(
                String.format("No available converter allowing to convert id from '%s' to '%s', <ids=%s>", currentFormat, format, ids)
        );
    }

    @Override
    public Map<ID,ID> to(final Format format, final ID firstId,  final ID... restIds) {
        checkNotNull(format);
        checkNotNull(firstId);
        checkNotNull(restIds);

        return to(format, Lists.asList(firstId, restIds));
    }

    @Override
    public ID to(final Format format, final ID id) {
        checkNotNull(format);
        checkNotNull(id);

        Map<ID,ID> transformedIds = to(format, id, new ID[0]);

        return transformedIds.get(id);
    }

    private Map<ID, ID> doNothing(final Collection<ID> ids) {
        return Maps.uniqueIndex(ids, new Function<ID, ID>() {
            @Override
            public ID apply(ID input) {
                return input;
            }
        });
    }

    protected boolean accept(final Converter converter, final String vendor, final Format sourceFormat, final Format targetFormat) {
        return  converter.getSource() == sourceFormat &&
                converter.getTarget() == targetFormat &&
                converter.getVendor().equals(vendor);
    }

    protected <T> boolean hasSameValue(final Collection<ID> ids, final Function<ID, T> function) {
        return ids.isEmpty() || Sets.newHashSet(Iterables.transform(ids, function)).size() == 1;
    }

    public <T> List<T> parseIdentifiers(final List<ID> ids) {
        return Lists.transform(ids, new Function<ID, T>() {
            @Override
            public T apply(ID input) {
                return input.<T>parseIdentifier();
            }
        });
    }

    public List<ID> toList(final Format format, final Collection<ID> ids) {
        return newArrayList(to(format, ids).values());
    }

}
