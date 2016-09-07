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
package com.viadeo.kasper.api.id;

import com.viadeo.kasper.api.exception.KasperException;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 *
 * A default {@link KasperID} implementation
 * @see KasperID
 *
 */
public class DefaultKasperRelationId extends AbstractKasperID<String> implements KasperRelationID {
    private static final long serialVersionUID = 2557821277131061279L;

    public static final String SEPARATOR = "--";

    protected KasperID sourceId;
    protected KasperID targetId;

    // ------------------------------------------------------------------------

    public static DefaultKasperRelationId random() {
        return new DefaultKasperRelationId();
    }

    // ------------------------------------------------------------------------

    public DefaultKasperRelationId() {
        this.sourceId = new DefaultKasperId(UUID.randomUUID());
        this.targetId = new DefaultKasperId(UUID.randomUUID());
        super.setId(relationIdsToString(this.sourceId, this.targetId));
    }

    public DefaultKasperRelationId(final KasperID sourceId, final KasperID targetId) {
        this.sourceId = checkNotNull(sourceId);
        this.targetId = checkNotNull(targetId);
        super.setId(relationIdsToString(this.sourceId, this.targetId));
    }

    public DefaultKasperRelationId(final String id) {
        final KasperID[] ids = stringToKasperIDs(id);

        this.sourceId = ids[0];
        this.targetId = ids[1];

        super.setId(id);
    }

    // ------------------------------------------------------------------------

    public void setId(final KasperID sourceId, final KasperID targetId) {
        this.sourceId = checkNotNull(sourceId);
        this.targetId = checkNotNull(targetId);
        super.setId(relationIdsToString(this.sourceId, this.targetId));
    }

    @Override
    public void setId(final String id) {
        final KasperID[] ids = stringToKasperIDs(id);

        this.sourceId = ids[0];
        this.targetId = ids[1];

        super.setId(id);
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperID getSourceId() {
        return this.sourceId;
    }

    @Override
    public KasperID getTargetId() {
        return this.targetId;
    }


    // ------------------------------------------------------------------------

    @Override
    public String toString() {
        return relationIdsToString(this.sourceId, this.targetId);
    }

    // ------------------------------------------------------------------------

    protected static KasperID[] stringToKasperIDs(final String id) {
        final String[] parts = id.split(SEPARATOR);

        if (parts.length != 2) {
            throw new KasperException("Unable to determine the two parts of a Kasper relation id from : " + id);
        }

        final KasperID[] ids = new KasperID[2];
        ids[0] = stringToKasperId(parts[0]);
        ids[1] = stringToKasperId(parts[1]);

        return ids;
    }

    protected static String relationIdsToString(final KasperID sourceId, final KasperID targetId) {
        return String.format("%s%s%s", sourceId.toString(), SEPARATOR, targetId.toString());
    }

    @SuppressWarnings("deprecation")
    protected static KasperID stringToKasperId(final String id) {
        try {
            final UUID uuid = UUID.fromString(id);
            return new DefaultKasperId(uuid);

        } catch (final IllegalArgumentException e) {
            try {
                final int intId = Integer.parseInt(id);
                return new IntegerKasperId(intId);

            } catch (final NumberFormatException e2) {
                return new StringKasperId(id);

            }
        }
    }

}



