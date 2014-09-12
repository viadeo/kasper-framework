// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.entities.relations.ids;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperRelationId;

public class GroupPermissionAssociationId extends DefaultKasperRelationId {

    public GroupPermissionAssociationId(final KasperID groupId, final KasperID permissionId){
        super(groupId, permissionId);
    }

    public KasperID getGroupId() {
        return (KasperID) this.getSourceId().getId();
    }

    public KasperID getPermissionId() {
        return (KasperID) this.getTargetId().getId();
    }

}

