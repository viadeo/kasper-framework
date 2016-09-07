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
package com.viadeo.kasper.common.exposition.adapters;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.common.exposition.TypeAdapter;
import com.viadeo.kasper.common.exposition.query.QueryFactory;

/**
 * The TypeAdapterFactory is useful when you want to mix custom serialization while still letting the
 * lib handle the rest. This is done by asking {@link com.viadeo.kasper.common.exposition.query.QueryFactory}
 * adapterFactory an instance of a TypeAdapter for a specific type.
 *
 * For example consider you want to always give the same name to all lists of DateTime:
 * <pre>
 * class MyTypeAdapterFactory implements TypeAdapterFactory&lt;MyPojo&gt; {
 * 
 *      Optional&lt;TypeAdapter&lt;MyPojo&gt;&gt; create(TypeToken&lt;MyPojo&gt; typeToken, IQueryFactory adapterFactory) {
 *              return Optional.fromNullable();
 *      }
 * }
 * 
 * // Define your custom TypeAdapter
 * class MyPojoTypeAdapter extends TypeAdapter&lt;MyPojo&gt; {
 *      private final TypeAdapte&lt;DateTime&gt; dateTimeAdapter;
 *      
 *      public ListOfUUIDTypeAdapter(TypeAdapte&lt;DateTime&gt; dateTimeAdapter) {
 *              this.dateTimeAdapter = dateTimeAdapter;
 *      }
 * 
 *       public void adapt(MyPojo pojo, QueryBuilder builder) {
 *           builder.addSingle("firstName", pojo.getName());
 *           builder.begin("birthDate");
 *           dateTimeAdapter.adapt(pojo.getBirthDate(), builder);
 *       }
 * }
 * 
 * class MyPojo implements IQuery {
 *      private String name;
 *      private DateTime birthDate;
 *      
 *      // getters
 * }
 * </pre>
 * 
 * @param <T> The kind of objects the TypeAdapters created by this factory can handle.
 */
public interface TypeAdapterFactory<T> {

    Optional<TypeAdapter<T>> create(TypeToken<T> typeToken, QueryFactory adapterFactory);

}
