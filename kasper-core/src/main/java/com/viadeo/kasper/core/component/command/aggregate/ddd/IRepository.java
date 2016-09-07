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
package com.viadeo.kasper.core.component.command.aggregate.ddd;

import com.viadeo.kasper.api.id.KasperID;

/**
 *
 * The base domain CQRS repository
 *
 * DDD Repository pattern hide the complexity of persisting business objects.
 *
 * CQRS repository does not allow retrieving entities by contract, but very discrete accesses of data
 * can be envisaged on some repositories. These read accesses has to be created for particular business
 * validations required by the model and should be optimized for access, using for instance a
 * dedicated storage index.
 *
 * @param <AGR> Aggregate root
 *
 * @see com.viadeo.kasper.api.component.Domain
 * @see AggregateRoot
 */
public interface IRepository<AGR extends AggregateRoot> extends org.axonframework.repository.Repository<AGR> {

	/**
	 * Generic parameter position of the AGR
	 */
	int ENTITY_PARAMETER_POSITION = 0;
	
	/**
	 * Initialize repository
	 */
	void init();

    /**
     * Checks if an aggregate if exists
     *
     * @param id the identifier
     * @return true if an aggregate exists with this id
     */
    boolean has(KasperID id);

    /**
     * Get an aggregate without planning further save on UOW commit
     *
     * Deprecated design : aggregates should only be loaded, with idea of change,
     * other data must be obtained from a query and passed to the command
     *
     * @param aggregateIdentifier the aggregate identifier to fetch
     * @param expectedVersion the aggregate expected version to fetch
     * @return the fetched aggregate if any
     */
    AGR get(KasperID aggregateIdentifier, Long expectedVersion);

    /**
     * Get an aggregate without planning further save on UOW commit
     *
     * Deprecated design : aggregates should only be loaded, with idea of change,
     * other data must be obtained from a query and passed to the command
     *
     * @param aggregateIdentifier the aggregate identifier
     * @return the fetched aggregate if any
     */
    AGR get(KasperID aggregateIdentifier);

}
