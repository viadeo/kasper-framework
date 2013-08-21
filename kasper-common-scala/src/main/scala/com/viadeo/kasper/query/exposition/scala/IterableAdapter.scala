package com.viadeo.kasper.query.exposition.scala

import com.viadeo.kasper.query.exposition.{ TypeAdapter, QueryBuilder, QueryParser }
import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions

class IterableAdapter[T](val valueAdapter: TypeAdapter[T]) extends TypeAdapter[Iterable[T]] {

  override def adapt(value: Iterable[T], builder: QueryBuilder) = {
    value foreach (
      valueAdapter.adapt(_, builder))
  }
  
  override def adapt(parser: QueryParser): Iterable[T] = {
    JavaConversions.asScalaIterable(parser).map(valueAdapter.adapt(_))
  }
}