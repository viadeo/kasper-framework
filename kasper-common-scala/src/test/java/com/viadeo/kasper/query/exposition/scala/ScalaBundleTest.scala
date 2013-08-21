package com.viadeo.kasper.query.exposition.scala

import org.scalatest.FunSuite
import com.viadeo.kasper.query.exposition.DefaultTypeAdapters
import com.viadeo.kasper.query.exposition.QueryBuilder


class ScalaBundleTest extends FunSuite {
 
    test("serialize scala Iterable of strings") {
      val iterable = List("a", "b", "c")
      val qBuilder = new QueryBuilder()
      
      new IterableAdapter(DefaultTypeAdapters.STRING_ADAPTER).adapt(iterable, qBuilder)
    }
}