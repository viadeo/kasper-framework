package com.viadeo.kasper.security.authz.entities.relations.ids;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperRelationId;

public class UserPermissionAssociationId extends DefaultKasperRelationId {

    public UserPermissionAssociationId(KasperID userId, KasperID permissionId){
        super(userId, permissionId);
    }

    public KasperID getUserId() {
        return (KasperID) this.getSourceId().getId();
    }

    public KasperID getPermissionId() {
        return (KasperID) this.getTargetId().getId();
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("userId", this.getUserId().toString())
                .add("permissionId", this.getPermissionId().toString()).toString();
    }

}

