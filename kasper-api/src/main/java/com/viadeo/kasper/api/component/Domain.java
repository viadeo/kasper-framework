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
package com.viadeo.kasper.api.component;


/**
 *
 * near of a DDD Bounded Context
 * 
 * All entities in this package are related to a domain by generalization
 * 
 * A domain communicates with other domains in three ways :
 * - handling commands
 * - sending domain events
 * - listening domain or external events
 *
 * In Kasper, query handlers are also binded to a domain, which can be the same than a command
 * domain, or can be a query-only dedicated domain.
 *
 ******
 * From domaindrivendesign.org :
 * 
 * Bounded Context
 * Definition: The delimited applicability of a particular model. BOUNDING CONTEXTS gives team members a clear and shared
 * understanding of what has to be consistent and what can develop independently.
 *
 * Problem: Multiple models are in play on any large project. Yet when code based on distinct models is combined,
 * software becomes buggy, unreliable, and difficult to understand. Communication among team members becomes confused.
 * It is often unclear in what context a model should not be applied.
 * 
 * Solution: Explicitly define the context within which a model applies. Explicitly set boundaries in terms of team
 * organization, usage within specific parts of the application, and physical manifestations such as code bases and
 * database schemas. Keep the model strictly consistent within these bounds, but don?t be distracted or confused by issues
 * outside.
 ******
 *
 */
public interface Domain {

}
