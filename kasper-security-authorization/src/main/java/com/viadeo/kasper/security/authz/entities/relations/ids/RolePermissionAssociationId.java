package com.viadeo.kasper.security.authz.entities.relations.ids;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperRelationId;

public class RolePermissionAssociationId extends DefaultKasperRelationId {

    public RolePermissionAssociationId(KasperID roleId, KasperID permissionId){
        super(roleId, permissionId);
    }

    public KasperID getRoleId() {
        return (KasperID) this.getSourceId().getId();
    }

    public KasperID getPermissionId() {
        return (KasperID) this.getTargetId().getId();
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("roleId", this.getRoleId().toString())
                .add("permissionId", this.getPermissionId().toString()).toString();
    }

}

