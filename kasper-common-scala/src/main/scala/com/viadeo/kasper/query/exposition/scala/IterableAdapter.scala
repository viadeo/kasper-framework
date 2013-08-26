// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition.scala


class IterableAdapter[T](val valueAdapter: TypeAdapter[T]) extends TypeAdapter[Iterable[T]] {

  override def adapt(value: Iterable[T], builder: QueryBuilder) = {
    value foreach (
      valueAdapter.adapt(_, builder))
  }
  
  override def adapt(parser: QueryParser): Iterable[T] = {
    JavaConversions.asScalaIterable(parser).map(valueAdapter.adapt(_))
  }
}
