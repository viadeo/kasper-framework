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

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Defines the requirement for an object responsible for "transforming" an <code>ID</code>
 */
public interface IDTransformer {

    /**
     * Transform the <code>ID</code>s according to the specified <code>Format</code>
     *
     * @param format the targeted format
     * @param ids the list of initial ids
     * @return a map of old vs transformed
     */
    Map<ID,ID> to(Format format, Collection<ID> ids);

    /**
     * Transform the <code>ID</code>s according to the specified <code>Format</code>
     *
     * @param format the targeted format
     * @param firstId the first id
     * @param restIds the rest of ids
     * @return a map of old vs transformed
     */
    Map<ID,ID> to(Format format, ID firstId,  ID... restIds);

    /**
     * @deprecated DO NOT USE, instead do: {@code new ArrayList<>(transformer.to(format, ids).values()); }
     */
    @Deprecated
    List<ID> toList(Format format, Collection<ID> ids);

    /**
     * Transform the <code>ID</code> according to the specified <code>Format</code>
     *
     * @param format the targeted format
     * @param id the initial id
     * @return a transformed id
     */
    ID to(Format format, ID id);
}
