// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.context.IDefaultContextBuilder;
import com.viadeo.kasper.impl.AbstractKasperID;

import java.util.UUID;

/**
 *
 * Default context builder used as a last chance if no other implementation can be found
 * @see IDefaultContextBuilder
 *
 */
public class DefaultContextBuilder implements IDefaultContextBuilder {
	
	/**
	 *
	 * A default {@link IContext} implementation
	 * @see IContext
	 * 
	 */
	public static class DefaultContext extends AbstractContext {

		private static final long serialVersionUID = -2357451589032314740L;
		
		public static final String DEFAULT_USER_LANG = "us";
		
		private IKasperID userId;
		private String userLang;
		
		public DefaultContext() {
			super();
			
			this.userId = new DefaultKasperId(UUID.randomUUID());
			this.userLang = DefaultContext.DEFAULT_USER_LANG;
		}		
		
		@Override
		public IKasperID getUserId() {
			return this.userId;
		}

		@Override
		public DefaultContext setUserId(final IKasperID userId) {
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
	 * A default {@link IKasperID} implementation
	 * @see IKasperID
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
	public IContext buildDefault() {
		return new DefaultContext();
	}

}
