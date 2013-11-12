package com.viadeo.kasper.security.query;


import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.cqrs.query.QueryMessage;
import com.viadeo.kasper.cqrs.query.QueryResponse;
import com.viadeo.kasper.cqrs.query.annotation.XKasperQueryHandler;
import com.viadeo.kasper.cqrs.query.exceptions.KasperQueryException;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryHandler;
import com.viadeo.kasper.security.query.results.IdentityResult;
import com.viadeo.kasper.security.Security;
import com.viaduc.util.encryption.Encryption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XKasperQueryHandler(domain = Security.class, description="The Security query handler")
public class GetIdentityQueryHandler extends AbstractQueryHandler<GetIdentityQuery, IdentityResult>{

    private static final Logger LOGGER = LoggerFactory.getLogger(GetIdentityQueryHandler.class);
    private static final int VIADEO_LEGACY_REMEMBERME_COOKIE_VERSION = 3;

	@Override
	public QueryResponse<IdentityResult> retrieve(final QueryMessage<GetIdentityQuery> message) throws KasperQueryException {
        String securityToken = message.getContext().getSecurityToken();
        try {
            final int memberId = getIdentityFromToken(securityToken);
            return QueryResponse.of(new IdentityResult(memberId));
        } catch (Exception e) {
            return QueryResponse.refused(KasperReason.Builder.from(CoreReasonCode.INVALID_ID, "Cannot decrypt security token").build());
        }
    }

	private int getIdentityFromToken(final String securityToken) throws Exception{
        int identity = 0;
        int version = Encryption.getVersion(securityToken);
        if (version == VIADEO_LEGACY_REMEMBERME_COOKIE_VERSION) {
            identity = Integer.parseInt(Encryption.decrypt(securityToken));
        }
        return identity;
	}

    private boolean isValidIdentity(final int memberId){
        boolean result = false;
        return result;
    }
}
