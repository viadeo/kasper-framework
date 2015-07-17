package com.viadeo.kasper.core.id;

import com.google.common.base.Objects;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.viadeo.kasper.api.id.Format;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class ConverterRegistry {

    private Map<String, Multimap<Format, Converter>> currentConvertersByFormatsByVendors = Maps.newHashMap();

    public void register(Converter converter) {
        checkNotNull(converter);

        Multimap<Format, Converter> currentConvertersByFormats = currentConvertersByFormatsByVendors.get(converter.getVendor());

        if (currentConvertersByFormats == null) {
            currentConvertersByFormats = ArrayListMultimap.create();
            currentConvertersByFormatsByVendors.put(converter.getVendor(), currentConvertersByFormats);
        }

        currentConvertersByFormats.put(converter.getTarget(), converter);
    }

    public Multimap<Format, Converter> getConvertersByFormats(String vendor) {
        return Objects.firstNonNull(currentConvertersByFormatsByVendors.get(vendor), ArrayListMultimap.<Format, Converter>create());
    }

    public Collection<Converter> getConverters(String vendor, Format format) {
        return getConvertersByFormats(vendor).get(format);
    }
}
