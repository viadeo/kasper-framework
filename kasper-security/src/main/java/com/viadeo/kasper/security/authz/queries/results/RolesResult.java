// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.queries.results;

import com.viadeo.kasper.cqrs.query.CollectionQueryResult;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryResult;

import java.util.List;

@XKasperQueryResult(description = "the roles authorization list")
public class RolesResult extends CollectionQueryResult<RoleResult> {

    public RolesResult(final List<RoleResult> roles) {
        super(roles);
    }

}
