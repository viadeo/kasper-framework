// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition.scala


class ScalaBundleTest extends FunSuite {
 
    test("serialize scala Iterable of strings") {
      val iterable = List("a", "b", "c")
      val qBuilder = new QueryBuilder()
      
      new IterableAdapter(DefaultTypeAdapters.STRING_ADAPTER).adapt(iterable, qBuilder)
    }
}
