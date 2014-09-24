// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.queries;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.cqrs.query.Query;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQuery;

@XKasperQuery(description = "get groups having the given role for authorization")
public class GetGroupsHavingRoleQuery implements Query {

    private final KasperID roleId;

    public GetGroupsHavingRoleQuery(final KasperID roleId) {
        this.roleId = roleId;
    }

    public KasperID getRoleId() {
        return roleId;
    }

}
