package com.viadeo.kasper.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.viadeo.kasper.KasperRelationID;

public class RelationID implements KasperRelationID {

    private final ID sourceId;
    private final ID targetId;

    public RelationID(ID sourceId, ID targetId) {
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    @Override
    public ID getSourceId() {
        return sourceId;
    }

    @Override
    public ID getTargetId() {
        return targetId;
    }

    @JsonIgnore
    @Override
    public Object getId() {
        return toString();
    }


    @Override
    public String toString() {
        return String.format("%s---%s", getSourceId(), getTargetId());
    }
}
