// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
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
