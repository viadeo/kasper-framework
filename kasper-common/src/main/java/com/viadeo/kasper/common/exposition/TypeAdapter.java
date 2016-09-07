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
// KASPER - Kasper is the treasure keeper
// www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
// Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.common.exposition;

import com.viadeo.kasper.common.exposition.query.QueryBuilder;
import com.viadeo.kasper.common.exposition.query.QueryParser;

/**
 * A type adapter allows to define how to build a query for a particular type of objects.
 *
 * Suppose for example you want to define some custom behavior for all instances of Lists of UUID.
 * Then you can just implement a custom TypeAdapter:
 *
 * <pre>
 * class ListOfUUIDTypeAdapter implements TypeAdapter&lt;List&lt;UUID&gt;&gt; {
 *       public void adapt(List&lt;UUID&gt; listOfUUID, QueryBuilder builder) {
 *           // first lets give
 *           builder.begin("list_of_uuid");
 *           for (UUID uuid : listOfUUID)
 *               builder.add(uuid.toString());
 *           builder.end();
 *       }
 *   }
 *   
 *   // now you only have to use KasperClientBuilder to configure your client in order to register your adapter.
 *   KasperClient client = new KasperClientBuilder().use(new ListOfUUIDTypeAdapter()).create();
 *   // and use it
 *   client.send(someCommand);
 * </pre>
 * 
 * If the TypeAdapter does fill your needs you might need to define a {@link com.viadeo.kasper.common.exposition.adapters.TypeAdapterFactory}.
 * 
 * @param <T> the type of object this adapter is dealing with.
 * 
 * @see com.viadeo.kasper.common.exposition.query.DefaultQueryFactory
 * @see com.viadeo.kasper.common.exposition.adapters.TypeAdapterFactory
 */
public interface TypeAdapter<T> {

    void adapt(T value, QueryBuilder builder) throws Exception;
    
    T adapt(QueryParser parser) throws Exception;

}
