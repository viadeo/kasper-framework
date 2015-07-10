package com.viadeo.kasper.api.id;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RelationID that = (RelationID) o;

        return Objects.equal(this.sourceId, that.sourceId) &&
                Objects.equal(this.targetId, that.targetId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sourceId, targetId);
    }
}
