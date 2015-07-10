package com.viadeo.kasper.core.ids;

import com.viadeo.kasper.api.Format;
import com.viadeo.kasper.api.ID;

import java.util.Collection;
import java.util.Map;

public interface Converter {
    Map<ID,ID> convert(Collection<ID> ids);
    Format getSource();
    Format getTarget();
    String getVendor();
}
