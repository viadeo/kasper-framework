// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.specification;

import com.viadeo.kasper.ddd.specification.impl.Specification;

class SpecificationStringIs extends Specification<String> {

    final private String isPattern;

    public SpecificationStringIs(final String isPattern) {
        this.isPattern = isPattern;
    }

    @Override
    public boolean isSatisfiedBy(final String entity) {
        return entity.contentEquals(this.isPattern);
    }

}
