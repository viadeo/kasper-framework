package com.viadeo.kasper.cqrs.query.impl.bean;

import java.io.Serializable;

import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryMessage;
import com.viadeo.kasper.cqrs.query.IQueryService;

public abstract class QueryBeanService<BEAN extends Serializable, DTO extends IQueryDTO> 
		implements IQueryService<BeanQuery<BEAN>, DTO> {

	@Override
	public DTO retrieve(final IQueryMessage<BeanQuery<BEAN>> message) {
		final QueryBeanMessage<BEAN> beanMessage;
		
		if (QueryBeanMessage.class.isAssignableFrom(message.getClass())) {
			beanMessage = (QueryBeanMessage<BEAN>) message;
		} else {
			beanMessage = new QueryBeanMessage<BEAN>(message.getContext(), message.getQuery());
		}
		
		return retrieve(beanMessage);
	}

	public abstract DTO retrieve(QueryBeanMessage<BEAN> message);	
	
}
