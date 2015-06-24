// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.saga.step;

import com.google.common.collect.Lists;

import java.util.List;

public class FacetApplierRegistry {

    private final List<FacetApplier> appliers;

    public FacetApplierRegistry() {
        this.appliers = Lists.newArrayList();
    }

    public void register(FacetApplier applier) {
        appliers.add(applier);
    }

    public List<FacetApplier> list() {
        return Lists.newArrayList(appliers);
    }
}
