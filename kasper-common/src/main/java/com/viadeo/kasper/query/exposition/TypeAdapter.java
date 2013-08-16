// ============================================================================
// KASPER - Kasper is the treasure keeper
// www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
// Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition;

import com.viadeo.kasper.query.exposition.query.QueryBuilder;
import com.viadeo.kasper.query.exposition.query.QueryParser;

/**
 * A type adapter allows to define how to build a query for a particular type of objects.
 * Suppose for example you want to define some custom behavior for all instances of Lists of UUID.
 * Then you can just implement a custom TypeAdapter:
 * <pre>
 * class ListOfUUIDTypeAdapter implements TypeAdapter&lt;List&lt;UUID>> {
 *       public void adapt(List&lt;UUID> listOfUUID, QueryBuilder builder) {
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
 * If the TypeAdapter does fill your needs you might need to define a {@link com.viadeo.kasper.query.exposition.adapters.TypeAdapterFactory}.
 * 
 * @param <T> the type of object this adapter is dealing with.
 * 
 * @see com.viadeo.kasper.query.exposition.query.DefaultQueryFactory
 * @see com.viadeo.kasper.query.exposition.adapters.TypeAdapterFactory
 */
public interface TypeAdapter<T> {

    void adapt(T value, QueryBuilder builder) throws Exception;
    
    T adapt(QueryParser parser) throws Exception;

}
