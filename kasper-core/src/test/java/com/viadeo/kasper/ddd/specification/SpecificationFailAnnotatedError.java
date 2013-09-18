// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd.specification;

import com.viadeo.kasper.ddd.specification.annotation.XSpecification;
import com.viadeo.kasper.ddd.specification.impl.Specification;

@XSpecification( description = "this specification should always fail", errorMessage = "it has failed" )
class SpecificationFailAnnotatedError extends Specification<String> {

    @Override
    public boolean isSatisfiedBy(final String entity) {
        return false;
    }

}
