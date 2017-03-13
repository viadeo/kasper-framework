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
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.component.query;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.viadeo.kasper.api.id.KasperID;
import org.joda.time.DateTime;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class EntityQueryResult implements QueryResult {

    private final KasperID id;
    private final String type;
    private final Long version;
    private final DateTime lastModificationDate;

    // ------------------------------------------------------------------------

    public EntityQueryResult(final KasperID id, final String type, final Long version) {
        this(id, type, version, new DateTime(0L));
    }

    public EntityQueryResult(final KasperID id, final String type, final Long version, final DateTime time) {
        this.id = checkNotNull(id);
        this.type = checkNotNull(type);
        this.version = checkNotNull(version);
        this.lastModificationDate = checkNotNull(time);
    }

    // ------------------------------------------------------------------------

    public KasperID getId() {
        return this.id;
    }

    public String getType() {
        return this.type;
    }

    public Long getVersion() {
        return this.version;
    }

    public DateTime getLastModificationDate() {
        return this.lastModificationDate;
    }

    // ------------------------------------------------------------------------

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id, this.type, this.version, this.lastModificationDate);
    }

    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        }

        if (this == checkNotNull(obj)) {
            return true;
        }

        if (!getClass().equals(obj.getClass())) {
            return false;
        }

        final EntityQueryResult other = (EntityQueryResult) obj;

        return Objects.equal(this.id, other.id) &&
                Objects.equal(this.type, other.type) &&
                Objects.equal(this.version, other.version) &&
                Objects.equal(this.lastModificationDate, other.lastModificationDate);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .addValue(this.id)
                .addValue(this.type)
                .addValue(this.version)
                .addValue(this.lastModificationDate)
                .toString();
    }

}
