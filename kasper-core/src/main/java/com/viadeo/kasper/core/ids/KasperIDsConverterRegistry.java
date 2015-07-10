// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.ids;

import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.viadeo.kasper.api.id.Format;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class KasperIDsConverterRegistry {

    private Map<String, Multimap<Format, Converter>> currentConvertersByFormatsByVendors = Maps.newHashMap();

    public void register(final Converter converter) {
        checkNotNull(converter);

        Multimap<Format, Converter> currentConvertersByFormats = currentConvertersByFormatsByVendors.get(converter.getVendor());

        if (null == currentConvertersByFormats) {
            currentConvertersByFormats = ArrayListMultimap.create();
            currentConvertersByFormatsByVendors.put(converter.getVendor(), currentConvertersByFormats);
        }

        currentConvertersByFormats.put(converter.getTarget(), converter);
    }

    protected Multimap<Format, Converter> getConvertersByFormats(final String vendor) {
        return Objects.firstNonNull(currentConvertersByFormatsByVendors.get(vendor), ArrayListMultimap.<Format, Converter>create());
    }

    public Collection<Converter> getConverters(final String vendor, final Format format) {
        return getConvertersByFormats(vendor).get(format);
    }

}
