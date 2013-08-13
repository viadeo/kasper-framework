// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.ddd.specification;

import com.viadeo.kasper.ddd.specification.impl.Specification;

class SpecificationStringContains extends Specification<String> {

    final private String containsPattern;

    public SpecificationStringContains(final String containsPattern) {
        this.containsPattern = containsPattern;
    }

    @Override
    public boolean isSatisfiedBy(final String entity) {
        return entity.contains(this.containsPattern);
    }
}
