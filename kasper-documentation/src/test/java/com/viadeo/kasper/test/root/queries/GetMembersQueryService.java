package com.viadeo.kasper.test.root.queries;

import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.cqrs.query.IQuery;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.IQueryMessage;
import com.viadeo.kasper.cqrs.query.IQueryService;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryService;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.test.root.Facebook;

@XKasperQueryService(domain=Facebook.class)
public class GetMembersQueryService implements IQueryService<GetMembersQueryService.Q, GetMembersQueryService.DTO> {

	public static class Q implements IQuery {
		private static final long serialVersionUID = -6513893864054353478L;
		public String name;
	}
	
	public static class DTO implements IQueryDTO {
		private static final long serialVersionUID = -2174693040511999516L;
		public String lastName;
		public String firstName;
		public IKasperID id;
	}

	@Override
	public DTO retrieve(final IQueryMessage<Q> message) throws KasperQueryException {
		return null;
	}
	
}
