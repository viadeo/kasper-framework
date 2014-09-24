// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.entities.relations.ids;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperRelationId;

public class RolePermissionAssociationId extends DefaultKasperRelationId {

    public RolePermissionAssociationId(final KasperID roleId, final KasperID permissionId){
        super(roleId, permissionId);
    }

    public KasperID getRoleId() {
        return (KasperID) this.getSourceId().getId();
    }

    public KasperID getPermissionId() {
        return (KasperID) this.getTargetId().getId();
    }

}

