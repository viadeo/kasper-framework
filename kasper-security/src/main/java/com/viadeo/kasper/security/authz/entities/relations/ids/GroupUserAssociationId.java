// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.security.authz.entities.relations.ids;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.impl.DefaultKasperRelationId;

public class GroupUserAssociationId extends DefaultKasperRelationId {

    public GroupUserAssociationId(final KasperID groupId, final KasperID userId){
        super(groupId, userId);
    }

    public KasperID getGroupId() {
        return (KasperID) this.getSourceId().getId();
    }

    public KasperID getUserId() {
        return (KasperID) this.getTargetId().getId();
    }

}

