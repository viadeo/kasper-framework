package com.viadeo.kasper.context.impl;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.context.ImmutableContext;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractImmutableContext implements ImmutableContext {

    private static final String KASPER_CID_SHORTNAME = "kcid";
    private static final String SEQ_INC_SHORTNAME = "seq";

    private Map<String, Serializable> properties;
    private KasperID kasperCorrelationId = DEFAULT_KASPERCORR_ID;

    /* Used to check child contexts, NOT USED IN EQUALITY CHECKS */
    int sequenceIncrement = INITIAL_SEQUENCE_INCREMENT;

    protected AbstractImmutableContext() {
    }

    /**
     * @return the Kasper correlation id
     */
    public KasperID getKasperCorrelationId() {
        return this.kasperCorrelationId;
    }


    @Override
    public Optional<Serializable> getProperty(final String key) {
        checkNotNull(key);
        final Optional<Serializable> ret;
        if (null == this.properties) {
            ret = Optional.absent();
        } else {
            ret = Optional.fromNullable(this.properties.get(key));
        }
        return ret;
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

    @Override
    public boolean hasProperty(final String key) {
        checkNotNull(key);
        return (null != this.properties) && this.properties.containsKey(key);
    }


    @Override
    public void incSequence() {
        this.sequenceIncrement++;
    }


    @Override
    public int getSequenceIncrement() {
        return this.sequenceIncrement;
    }


    @Override
    public Map<String, String> asMap() {
        final Map<String, String> retMap = Maps.newHashMap();
        return asMap(retMap);
    }

    protected String safeStringObject(final Serializable unsafeObject) {
        if (null == unsafeObject) {
            return "";
        }
        return unsafeObject.toString();
    }

    @Override
    public Map<String, String> asMap(final Map<String, String> origMap) {
        final Map<String, String> retMap;

        if (null == origMap) {
            retMap = Maps.newHashMap();
        } else {
            retMap = origMap;
        }

        if (null != this.properties) {
            for (final Map.Entry<String, Serializable> entry : this.properties.entrySet()) {
                retMap.put(entry.getKey(), safeStringObject(entry.getValue()));
            }
        }

        retMap.put(KASPER_CID_SHORTNAME, safeStringObject(this.kasperCorrelationId));
        retMap.put(SEQ_INC_SHORTNAME, safeStringObject(this.sequenceIncrement));

        return retMap;
    }

    @Override
    public boolean equals(final Object obj) {
        if (null == obj) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        final AbstractImmutableContext other = (AbstractImmutableContext) obj;

        final boolean equals = Objects.equal(this.kasperCorrelationId, other.getKasperCorrelationId());

        if (!equals) {
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
            if (!other.hasProperty(entry.getKey())) {
                return false;
            }
            if (!other.getProperty(entry.getKey()).get().equals(entry.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.kasperCorrelationId, this.properties);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .addValue(this.kasperCorrelationId)
                .addValue(this.sequenceIncrement)
                .addValue(this.properties)
                .toString();
    }

}
