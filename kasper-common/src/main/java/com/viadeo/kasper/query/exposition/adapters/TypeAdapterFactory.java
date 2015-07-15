// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition.adapters;

import com.google.common.base.Optional;
import com.google.common.reflect.TypeToken;
import com.viadeo.kasper.query.exposition.TypeAdapter;
import com.viadeo.kasper.query.exposition.query.QueryFactory;

/**
 * The TypeAdapterFactory is useful when you want to mix custom serialization while still letting the
 * lib handle the rest. This is done by asking {@link com.viadeo.kasper.query.exposition.query.QueryFactory} 
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
