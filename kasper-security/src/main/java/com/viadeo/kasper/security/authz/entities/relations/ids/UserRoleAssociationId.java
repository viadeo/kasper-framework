// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.entities.relations.ids;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperRelationId;

public class UserRoleAssociationId extends DefaultKasperRelationId {

    public UserRoleAssociationId(final KasperID userId, final KasperID roleId){
        super(userId, roleId);
    }

    public KasperID getUserId() {
        return (KasperID) this.getSourceId().getId();
    }

    public KasperID getRoleId() {
        return (KasperID) this.getTargetId().getId();
    }

}

