// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.ids;

import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.ID;

import java.util.Collection;
import java.util.Map;

public interface Converter {

    Map<ID,ID> convert(Collection<ID> ids);

    Format getSource();

    Format getTarget();

    String getVendor();

}
