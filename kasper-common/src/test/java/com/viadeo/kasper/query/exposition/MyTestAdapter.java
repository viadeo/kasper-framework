package com.viadeo.kasper.query.exposition;

public class MyTestAdapter implements ITypeAdapter<Integer>{

	@Override
	public void adapt(Integer value, QueryBuilder builder) {
		
	}

	@Override
	public Integer adapt(QueryParser parser) {
		return null;
	}

}
