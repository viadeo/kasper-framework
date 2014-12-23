// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.viadeo.kasper.KasperID;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A representation of an URN. The goal is to have only one implementation representing a lot and various kind of
 * identifier.
 *
 * <p>The supported format by <code>ID</code> are defined by <code>Format</code>
 *
 * <p> An <code>ID</code> can be transformed to an another <code>Format</code> by transformation thanks to
 * <code>IDTransformer</code>
 *
 *
 * <h4> How to instantiate an <code>ID</code> </h4>
 *
 * <p>The typical instantiation sequences are thus
 *
 * <blockquote><pre>
 * ID id1 = new ID(Vendors.VIADEO, "member", Format.DB_ID, 42);
 * ID id2 = new ID("urn:viadeo:member:db-id:42");
 * Assert.assertEquals(id1, id2)
 * </pre></blockquote>
 *
 *
 * <h4> The URN layout </h4>
 *
 * <tt>urn:[vendor]:[type]:[format]:[identifier]</tt>
 *
 * <ul>
 *     <li><b>vendor</b>: the vendor of the URN</li>
 *     <li><b>type</b>: the object type for which the identifier refer</li>
 *     <li><b>format</b>: the format of the identifier</li>
 *     <li><b>identifier</b>: the identifier</li>
 * </ul>
 *
 *
 * <h4> Example of URNs </h4>
 *
 * <ul>
 *     <li><i>urn:viadeo:member:db-id:42</i> the database id of a member</li>
 *     <li><i>urn:viadeo:member:web-id:0021xp3coi0ns47i</i> the web id of a member</li>
 *     <li><i>urn:viadeo:member:uuid:3b3f42b6-7dfe-43d1-ad9a-9e16f2d63a44</i> the uuid of a member</li>
 *     <li><i>urn:viadeo:member:graph-id:skvhcwhInusoDgzrrehurugbOo</i> the graph id of a member</li>
 * </ul>
 */
public class ID implements KasperID {

    private final String vendor;

    private final String type;

    private final Format format;

    private final String identifier;

    private transient IDTransformer transformer;

    public ID(final String vendor, final String type, final Format format, final Object identifier) {
        checkNotNull(identifier);
        checkArgument(format.accept(identifier), "The identifier is incompatible with the format");

        this.vendor = checkNotNull(vendor);
        this.type = checkNotNull(type);
        this.format = checkNotNull(format);
        this.identifier = String.valueOf(identifier);
    }

    /**
     * @return the vendor of the ID
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * @return the object type for which the identifier refer
     */
    public String getType() {
        return type;
    }

    /**
     * @return the format of the identifier
     */
    public Format getFormat() {
        return format;
    }

    /**
     * Parse the identifier
     * @return the object value represented by the <code>Format</code> of this <code>ID</code>.
     * @exception ClassCastException the inferred type is incompatible with the <code>Format</code>
     * @exception IllegalArgumentException the identifier is incompatible with the <code>Format</code>
     */
    @SuppressWarnings("unchecked")
    public <E> E parseIdentifier() {
        return format.parseIdentifier(identifier);
    }


    /**
     * @return the identifier
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * This method is here to be compatible with the interface KasperId.
     * We can consider this method as an alias of the getIdentifier method.
     * @return the identifier
     */
    @Override
    public Object getId() {
        return getIdentifier();
    }

    /**
     * Ensures that this instance of <code>ID</code> match the vendor specified as a parameter.
     *
     * @param expectedVendor the expected vendor for which this instance should match
     * @exception IllegalStateException if the specified vendor doesn't match the vendor of this instance of <code>ID</code>
     */
    public ID checkVendor(final String expectedVendor) {
        Preconditions.checkState(
                this.vendor.equals(expectedVendor),
                String.format("Unexpected vendor, expected:<%s> but was:<%s>", expectedVendor, vendor)
        );
        return this;
    }

    /**
     * Ensures that this instance of <code>ID</code> match the object type specified as a parameter.
     *
     * @param expectedType the expected object type for which this instance should match
     * @exception IllegalStateException if the specified vendor doesn't match the type of this instance of <code>ID</code>
     */
    public ID checkType(final String expectedType) {
        Preconditions.checkState(
                this.type.equals(expectedType),
                String.format("Unexpected type, expected:<%s> but was:<%s>", expectedType, type)
        );
        return this;
    }

    /**
     * Ensures that this instance of <code>ID</code> match the format specified as a parameter.
     *
     * @param expectedFormat the expected object type for which this instance should match
     * @exception IllegalStateException if the specified format doesn't match the format of this instance of <code>ID</code>
     */
    public ID checkFormat(final Format expectedFormat) {
        Preconditions.checkState(
                this.format.equals(expectedFormat),
                String.format("Unexpected format, expected:<%s> but was:<%s>", expectedFormat, format)
        );
        return this;
    }

    /**
     * Transform according to the specified <code>Format</code>
     *
     * @param format the targeted format
     * @return a transformed id
     */
    public ID to(Format format) {
        if (null == transformer) {
            throw new UnsupportedOperationException("IDTransformer is not defined");
        }
        return transformer.to(format, this).withTransformer(transformer);
    }

    /**
     * Returns an optional transformer
     *
     * @return the id transformer
     */
    public Optional<IDTransformer> getTransformer() {
        return Optional.fromNullable(transformer);
    }

    public void setIDTransformer(IDTransformer transformer) {
        this.transformer = transformer;
    }

    public ID withTransformer(IDTransformer transformer) {
        setIDTransformer(transformer);
        return this;
    }

    @Override
    public String toString() {
        return String.format("urn:%s:%s:%s:%s", vendor, type, format, identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(vendor, type, format, identifier);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final ID other = (ID) obj;
        return Objects.equal(this.vendor, other.vendor) &&
                Objects.equal(this.type, other.type) &&
                Objects.equal(this.format, other.format) &&
                Objects.equal(this.identifier, other.identifier);
    }
}