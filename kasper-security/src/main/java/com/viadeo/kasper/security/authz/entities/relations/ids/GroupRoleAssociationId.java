// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.entities.relations.ids;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperRelationId;

public class GroupRoleAssociationId extends DefaultKasperRelationId {

    public GroupRoleAssociationId(final KasperID groupId, final KasperID roleId) {
        super(groupId, roleId);
    }

    public KasperID getGroupId() {
        return (KasperID) this.getSourceId().getId();
    }

    public KasperID getRoleId() {
        return (KasperID) this.getTargetId().getId();
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("groupId", this.getGroupId().toString())
                .add("roleId", this.getRoleId().toString()).toString();
    }

}

