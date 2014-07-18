package com.viadeo.kasper.security.authz.entities.relations.ids;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperRelationId;

public class UserRoleAssociationId extends DefaultKasperRelationId {

    public UserRoleAssociationId(KasperID userId, KasperID roleId){
        super(userId, roleId);
    }

    public KasperID getUserId() {
        return (KasperID) this.getSourceId().getId();
    }

    public KasperID getRoleId() {
        return (KasperID) this.getTargetId().getId();
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("userId", this.getUserId().toString())
                .add("roleId", this.getRoleId().toString()).toString();
    }

}

