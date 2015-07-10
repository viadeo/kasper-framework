// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.ids;

import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimaps;
import com.viadeo.kasper.api.Format;
import com.viadeo.kasper.api.ID;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractByTypeConverter extends AbstractConverter {

    public AbstractByTypeConverter(final String vendor, final Format source, final Format target) {
        super(vendor, source, target);
    }

    // ------------------------------------------------------------------------

    public abstract Map<ID, ID> doConvert(final String type, final Collection<ID> ids);

    @Override
    public Map<ID,ID> convert(final Collection<ID> ids) {

        final ListMultimap<String, ID> idsByType = Multimaps.index(ids, new Function<ID, String>() {
            @Override
            public java.lang.String apply(com.viadeo.kasper.api.ID input) {
                return checkNotNull(input).getType();
            }
        });

        final Map<ID,ID> convertResults = Maps.newHashMapWithExpectedSize(ids.size());

        for (final String type : idsByType.keySet()) {
            convertResults.putAll(doConvert(type, idsByType.get(type)));
        }

        return convertResults;
    }

}
