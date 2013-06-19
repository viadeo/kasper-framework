// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.impl.AbstractKasperID;

import java.util.UUID;

/**
 *
 * Default context builder used as a last chance if no other implementation can be found
 * @see com.viadeo.kasper.context.DefaultContextBuilder
 *
 */
public class DefaultContextBuilder implements com.viadeo.kasper.context.DefaultContextBuilder {
	
	/**
	 *
	 * A default {@link com.viadeo.kasper.context.Context} implementation
	 * @see com.viadeo.kasper.context.Context
	 * 
	 */
	public static class DefaultContext extends AbstractContext {

		private static final long serialVersionUID = -2357451589032314740L;
		
		public static final String DEFAULT_USER_LANG = "us";
		
		private KasperID userId;
		private String userLang;
		
		public DefaultContext() {
			super();
			
			this.userId = new DefaultKasperId(UUID.randomUUID());
			this.userLang = DefaultContext.DEFAULT_USER_LANG;
		}		
		
		@Override
		public KasperID getUserId() {
			return this.userId;
		}

		@Override
		public DefaultContext setUserId(final KasperID userId) {
			this.userId = userId;
			return this;
		}

		@Override
		public String getUserLang() {
			return this.userLang;
		}

		@Override
		public void setUserLang(final String userLang) {
			this.userLang = userLang;
		}

	}

	// ------------------------------------------------------------------------

	/**
	 *
	 * A default {@link com.viadeo.kasper.KasperID} implementation
	 * @see com.viadeo.kasper.KasperID
	 * 
	 */
	public static class DefaultKasperId extends AbstractKasperID<UUID> {
		private static final long serialVersionUID = 2557821277131061279L;

		protected DefaultKasperId() {
			super(UUID.randomUUID());
		}
		
		public DefaultKasperId(final UUID userId) {
			super(userId);
		}
		
		public DefaultKasperId(final String userId) {
			super(UUID.fromString(userId));
		}

	}

	// ------------------------------------------------------------------------

	@Override
	public Context buildDefault() {
		return new DefaultContext();
	}

}
