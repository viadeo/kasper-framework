package com.viadeo.kasper.security.authz.entities.relations.ids;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperRelationId;

public class GroupUserAssociationId extends DefaultKasperRelationId {

    public GroupUserAssociationId(KasperID groupId, KasperID userId){
        super(groupId, userId);
    }

    public KasperID getGroupId() {
        return (KasperID) this.getSourceId().getId();
    }

    public KasperID getUserId() {
        return (KasperID) this.getTargetId().getId();
    }

    @Override
    public String toString() {
        return com.google.common.base.Objects.toStringHelper(this)
                .add("groupId", this.getGroupId().toString())
                .add("userId", this.getUserId().toString()).toString();
    }

}

