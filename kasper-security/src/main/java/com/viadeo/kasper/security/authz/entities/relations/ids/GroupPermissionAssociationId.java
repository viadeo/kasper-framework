package com.viadeo.kasper.security.authz.entities.relations.ids;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperRelationId;

public class GroupPermissionAssociationId extends DefaultKasperRelationId {

    public  GroupPermissionAssociationId(KasperID groupId, KasperID permissionId){
        super(groupId, permissionId);
    }

    public KasperID getGroupId() {
        return (KasperID) this.getSourceId().getId();
    }

    public KasperID getPermissionId() {
        return (KasperID) this.getTargetId().getId();
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("groupId", this.getGroupId().toString())
                .add("permissionId", this.getPermissionId().toString()).toString();
    }

}

