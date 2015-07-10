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

    public AbstractByTypeConverter(String vendor, Format source, Format target) {
        super(vendor, source, target);
    }

    public abstract Map<ID, ID> doConvert(String type, Collection<ID> ids);

    @Override
    public Map<ID,ID> convert(Collection<ID> ids) {
        ListMultimap<String, ID> idsByType = Multimaps.index(ids, new Function<ID, String>() {
            @Override
            public java.lang.String apply(com.viadeo.kasper.api.ID input) {
                return checkNotNull(input).getType();
            }
        });

        final Map<ID,ID> convertResults = Maps.newHashMapWithExpectedSize(ids.size());

        for (String type : idsByType.keySet()) {
            convertResults.putAll(doConvert(type, idsByType.get(type)));
        }

        return convertResults;
    }
}
