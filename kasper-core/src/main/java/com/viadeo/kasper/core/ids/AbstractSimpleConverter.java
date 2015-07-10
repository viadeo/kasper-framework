package com.viadeo.kasper.core.ids;

import com.google.common.collect.Maps;
import com.viadeo.kasper.api.Format;
import com.viadeo.kasper.api.ID;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractSimpleConverter extends AbstractConverter {

    public AbstractSimpleConverter(String vendor, Format source, Format target) {
        super(vendor, source, target);
    }

    @Override
    public Map<ID,ID> convert(Collection<ID> ids) {
        Map<ID,ID> transformedIdToIds = Maps.newHashMapWithExpectedSize(ids.size());

        for (ID id:ids) {
            transformedIdToIds.put(id, convert(id));
        }

        return transformedIdToIds;
    }

    public abstract ID convert(ID id);

}
