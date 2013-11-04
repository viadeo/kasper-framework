// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.impl.DefaultKasperId;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract implementation of the Context
 *
 * Provides the Kasper correlation id
 *
 */
public abstract class AbstractContext implements Context {
	private static final long serialVersionUID = 1887660968377933167L;

	private Map<String, Serializable> properties;
    private KasperID kasperCorrelationId = DEFAULT_KASPERCORR_ID;

    /* Used to check child contexts, NOT USED IN EQUALITY CHECKS */
    private int sequenceIncrement = INITIAL_SEQUENCE_INCREMENT;

	// ------------------------------------------------------------------------
	
	protected AbstractContext() { /* abstract */ }

    // ------------------------------------------------------------------------

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
        this.kasperCorrelationId = kasperCorrelationId;
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

    /**
     * @return the Kasper correlation id
     */
    public KasperID getKasperCorrelationId() {
        return this.kasperCorrelationId;
    }

    // ------------------------------------------------------------------------

	@Override
	public Context setProperty(final String key, final Serializable value) {
		if (null == this.properties) {
			this.properties = Maps.newHashMap();
		}
		this.properties.put(key, value);
        return this;
	}

	@Override
	public Optional<Serializable> getProperty(final String key) {
		final Optional<Serializable> ret;
		if (null == this.properties) {
			ret = Optional.absent();
		} else {
			ret = Optional.fromNullable(this.properties.get(key));
		}
		return ret;
	}

	@Override
	public boolean hasProperty(final String key) {
        return (null != this.properties) && this.properties.containsKey(key);
	}

	@Override
	public Map<String, Serializable> getProperties() {
		final Map<String, Serializable> retMap;
		
		if (null == this.properties) {
			retMap = Maps.newLinkedHashMap();
		} else {
			retMap = Collections.unmodifiableMap(this.properties);
		}
		
		return retMap;
	}

    // ------------------------------------------------------------------------

    @Override
    public int getSequenceIncrement() {
        return this.sequenceIncrement;
    }

    @Override
    public Context child() {
        final AbstractContext newContext;

        try {
            newContext = this.getClass().newInstance();
        } catch (final InstantiationException e) {
            throw new KasperException("Unable to clone context", e);
        } catch (final IllegalAccessException e) {
            throw new KasperException("Unable to clone context", e);
        }

        if (null != this.properties) {
            newContext.properties = Maps.newHashMap(this.properties);
        }

        newContext.kasperCorrelationId = this.kasperCorrelationId;
        newContext.sequenceIncrement = this.sequenceIncrement + 1;

        return newContext;
    }

    // ------------------------------------------------------------------------

    @Override
    public boolean equals(Object obj)
    {
        if (null == obj) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final AbstractContext other = (AbstractContext) obj;

        final boolean equals = Objects.equal(this.kasperCorrelationId, other.getKasperCorrelationId());

        if ( ! equals) {
            return false;
        }

        if ((null == this.properties) && (null == other.properties)) {
            return true;
        }

        if ((null == this.properties) || (null == other.properties)) {
            return false;
        }

        if (this.properties.size() != other.properties.size()) {
            return false;
        }

        for (final Map.Entry<String, Serializable> entry : this.properties.entrySet()) {
            if ( ! other.hasProperty(entry.getKey())) {
                return false;
            }
            if ( ! other.getProperty(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode( this.kasperCorrelationId, this.properties);
    }

    @Override
    public String toString()
    {
        return Objects.toStringHelper(this)
                .addValue(this.kasperCorrelationId)
                .addValue(this.sequenceIncrement)
                .addValue(this.properties)
                .toString();
    }

}
