// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;

public class SimpleIDBuilder implements IDBuilder {

    private static final Pattern URN_PATTERN = Pattern.compile("^urn:([a-zA-Z]+):([a-zA-Z\\-]+):([a-zA-Z\\-]+):(.+)$");

    public final Map<String, Format> formatByName;

    public SimpleIDBuilder(final Format... formats) {
        this.formatByName = Maps.newHashMap();

        for (final Format format : formats) {
            formatByName.put(format.name(), format);
        }
    }
    @Override
    public ID build(final String urn) {
        Preconditions.checkNotNull(urn);

        final Matcher matcher = URN_PATTERN.matcher(urn);

        if ( ! matcher.find()) {
            throw new IllegalArgumentException(
                    String.format("Invalid URN layout, <URN=%s>", urn)
            );
        }

        final String formatAsString = checkNotNull(matcher.group(3));

        final Format format = formatByName.get(formatAsString);

        if (format == null) {
            throw new IllegalArgumentException(
                    String.format("Invalid URN format, format not supported '%s'", formatAsString)
            );
        }

        return new ID(
                checkNotNull(matcher.group(1)),
                checkNotNull(matcher.group(2)),
                format,
                format.parseIdentifier(checkNotNull(matcher.group(4)))
        );
    }

    @Override
    public Collection<Format> getSupportedFormats() {
        return Lists.newArrayList(formatByName.values());
    }
}
