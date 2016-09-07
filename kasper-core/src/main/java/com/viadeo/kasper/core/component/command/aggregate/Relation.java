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
package com.viadeo.kasper.core.component.command.aggregate;

import com.viadeo.kasper.api.id.KasperID;
import com.viadeo.kasper.api.id.KasperRelationID;
import com.viadeo.kasper.core.component.command.aggregate.annotation.XBidirectional;
import com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot;
import com.viadeo.kasper.core.component.command.aggregate.ddd.Entity;

/**
 *
 * A aggregate root for Kasper relation
 *
 * @param <S> Source of the relation
 * @param <T> Target of the relation
 * 
 * @see Relation
 * @see com.viadeo.kasper.core.component.command.aggregate.ddd.AggregateRoot
 */
public class Relation<S extends Concept, T extends Concept>
        extends AggregateRoot<KasperRelationID>
		implements Entity {

    private static final long serialVersionUID = 4719442806097449770L;

    /**
     * The position of the source concept parameter
     */
    public static final Integer SOURCE_PARAMETER_POSITION = 0;

    /**
     * The position of the target concept parameter
     */
    public static final Integer TARGET_PARAMETER_POSITION = 1;

    // ------------------------------------------------------------------------

    public KasperID getSourceIdentifier() {
        return this.getEntityId().getSourceId();
    }

    public KasperID getTargetIdentifier() {
        return this.getEntityId().getTargetId();
    }

    public boolean isBidirectional() {
        return (null != this.getClass().getAnnotation(XBidirectional.class));
    }

}
