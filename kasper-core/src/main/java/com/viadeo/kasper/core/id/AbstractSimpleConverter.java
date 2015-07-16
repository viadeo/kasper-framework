// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.ID;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractSimpleConverter extends AbstractConverter {

    public AbstractSimpleConverter(final String vendor, final Format source, final Format target) {
        super(vendor, source, target);
    }

    // ------------------------------------------------------------------------

    @Override
    public Map<ID,ID> convert(final Collection<ID> ids) {
        final Map<ID,ID> transformedIdToIds = Maps.newHashMapWithExpectedSize(ids.size());

        for (final ID id : ids) {
            transformedIdToIds.put(id, convert(id));
        }

        return transformedIdToIds;
    }

    public abstract ID convert(ID id);

}
