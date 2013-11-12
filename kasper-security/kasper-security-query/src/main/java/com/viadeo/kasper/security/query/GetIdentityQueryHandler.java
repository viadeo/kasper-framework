package com.viadeo.kasper.security.query;


import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryHandler;
import com.viadeo.kasper.security.query.results.IdentityResult;
import com.viaduc.util.encryption.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XKasperQueryHandler(domain = com.viadeo.kasper.security.Security.class)
public class GetIdentityQueryHandler extends AbstractQueryHandler<GetIdentityQuery, IdentityResult>{

    static final int VIADEO_LEGACY_REMEMBERME_COOKIE_VERSION = 3;
    static final Logger LOGGER = LoggerFactory.getLogger(GetIdentityQueryHandler.class);

	@Override
	public QueryResponse<IdentityResult> retrieve(final QueryMessage<GetIdentityQuery> message) throws KasperQueryException {
        String securityToken = message.getContext().getSecurityToken();
		int memberId = getIdentityFromToken(securityToken);
		return QueryResponse.of(new IdentityResult(memberId));
	}

	private int getIdentityFromToken(final String securityToken) {
        int version = Encryption.getVersion(securityToken);
        int identity = 0;
        try{
            if(version == VIADEO_LEGACY_REMEMBERME_COOKIE_VERSION){
                identity = Integer.parseInt(Encryption.decrypt(securityToken));
            }
        }catch(Exception e){
            LOGGER.error("Cannot decrypt security token", e);
        }
		return identity;
	}

}
