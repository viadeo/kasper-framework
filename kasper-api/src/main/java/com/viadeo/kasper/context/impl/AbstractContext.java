// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.google.common.collect.Maps;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.ImmutableContext;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.impl.DefaultKasperId;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Abstract implementation of the Context
 *
 * Provides the Kasper correlation id
 *
 */
@Deprecated
public abstract class AbstractContext extends AbstractImmutableContext implements Context {

    private static final long serialVersionUID = 1887660968377933167L;

    private Map<String, Serializable> properties;
    private KasperID kasperCorrelationId = DEFAULT_KASPERCORR_ID;
    private int sequenceIncrement = INITIAL_SEQUENCE_INCREMENT;

	protected AbstractContext() { /* abstract */ }

    /**
     * Sets the Kasper correlation id
     *
     * For one Kasper action (command, query or event sent), several other actions
     * can be sent, this correlation id can be used in order to track them all under
     * the same key.
     *
     * @param kasperCorrelationId the Kasper correlation id be used for this action
     */
    public void setKasperCorrelationId(final KasperID kasperCorrelationId) {
        this.kasperCorrelationId = checkNotNull(kasperCorrelationId);
    }

    /**
     * Sets a random Kasper correlation id, if it has not been set before
     */
    public void setValidKasperCorrelationId() {
        if (this.kasperCorrelationId.equals(DEFAULT_KASPERCORR_ID)) {
            this.kasperCorrelationId = new DefaultKasperId(UUID.randomUUID());
        }
    }

    /**
     * Sets a new random Kasper correlation id
     */
    public void setNewKasperCorrelationId() {
        this.kasperCorrelationId = new DefaultKasperId(UUID.randomUUID());
    }


	@Override
	public Context setProperty(final String key, final Serializable value) {
		if (null == this.properties) {
			this.properties = Maps.newHashMap();
		}
		this.properties.put(checkNotNull(key), value);
        return this;
	}

    @SuppressWarnings("unchecked") // must be ensured by client
    @Override
    public <C extends ImmutableContext> C child() {

        final AbstractContext newContext;

        try {
            newContext = this.getClass().newInstance();

        } catch (final InstantiationException | IllegalAccessException e) {
            throw new KasperException("Unable to clone context", e);
        }

        if (null != this.properties) {
            newContext.properties = Maps.newHashMap(this.properties);
        }

        newContext.kasperCorrelationId = this.kasperCorrelationId;
        newContext.sequenceIncrement = this.sequenceIncrement + 1;

        return (C) newContext;
    }

}
