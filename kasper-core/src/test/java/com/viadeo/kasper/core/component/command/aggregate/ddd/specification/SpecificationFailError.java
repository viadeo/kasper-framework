// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command.aggregate.ddd.specification;

import com.viadeo.kasper.core.component.command.aggregate.ddd.specification.annotation.XKasperSpecification;
import com.viadeo.kasper.core.component.command.aggregate.ddd.specification.impl.Specification;

@XKasperSpecification( description = "this specification should always fail" )
class SpecificationFailError extends Specification<String> {

    @Override
    public boolean isSatisfiedBy(final String entity) {
        return false;
    }

}
