package com.viadeo.kasper.cqrs.query.impl.bean;

import java.io.Serializable;

import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.cqrs.query.impl.QueryMessage;

public class QueryBeanMessage<BEAN extends Serializable> extends QueryMessage<BeanQuery<BEAN>> {

	private static final long serialVersionUID = -1253624028822092359L;
	
	public QueryBeanMessage(final IContext context, final BeanQuery<BEAN> query) {
		super(context, query);
	}

}
