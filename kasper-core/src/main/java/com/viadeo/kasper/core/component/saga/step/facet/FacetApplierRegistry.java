// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.saga.step.facet;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FacetApplierRegistry {

    private static final Comparator<FacetApplier> APPLIER_COMPARATOR = new Comparator<FacetApplier>() {
        @Override
        public int compare(FacetApplier o1, FacetApplier o2) {
        return o1.getPhase() - o2.getPhase();
        }
    };

    private final List<FacetApplier> appliers;

    // ------------------------------------------------------------------------

    public FacetApplierRegistry() {
        this.appliers = Lists.newArrayList();
    }

    // ------------------------------------------------------------------------

    public void register(final FacetApplier applier) {
        appliers.add(applier);
        Collections.sort(appliers, APPLIER_COMPARATOR);
    }

    public List<FacetApplier> list() {
        return Lists.newArrayList(appliers);
    }

}
