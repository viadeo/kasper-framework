// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.ID;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public final class Converters {

    private Converters() { }

    public static Converter chain(Converter firstConverter, Converter nextConverter) {
        checkNotNull(firstConverter);
        checkNotNull(nextConverter);
        checkArgument(firstConverter.getVendor().equals(nextConverter.getVendor()), "firstConverter and nextConverter must share the same vendor");

        if (firstConverter.getSource().equals(nextConverter.getTarget())) {
            return newIdentityConverter(firstConverter.getVendor(), firstConverter.getSource());
        }

        return new ChainedConverter(firstConverter, nextConverter);
    }

    private static class ChainedConverter
            extends AbstractConverter {

        private final Converter firstConverter;
        private final Converter nextConverter;

        public ChainedConverter(Converter firstConverter, Converter nextConverter) {
            super(firstConverter.getVendor(), firstConverter.getSource(), nextConverter.getTarget());
            checkArgument(firstConverter.getVendor().equals(nextConverter.getVendor()), "firstConverter and nextConverter must share the same vendor");
            checkArgument(firstConverter.getTarget().equals(nextConverter.getSource()), "the source format for nextConverter must be the target format for firstConverter");
            this.firstConverter = checkNotNull(firstConverter);
            this.nextConverter = checkNotNull(nextConverter);
        }

        @Override
        public Map<ID,ID> convert(Collection<ID> ids) {
            // givenId -> firstConversionId
            final Map<ID,ID> firstConversion = firstConverter.convert(ids);

            // firstConversionId -> nextConversionId
            final Map<ID, ID> nextConversion = nextConverter.convert(firstConversion.values());

            Map<ID, ID> result = Maps.newHashMap();

            for (ID id : ids) {
                ID firstConvertedId = firstConversion.get(id);
                if (firstConvertedId != null) {
                    result.put(id, nextConversion.get(firstConvertedId));
                }
            }
            return result;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(firstConverter, nextConverter);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ChainedConverter other = (ChainedConverter) obj;
            return Objects.equal(this.firstConverter, other.firstConverter) && Objects.equal(this.nextConverter, other.nextConverter);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("firstConverter", firstConverter)
                    .add("nextConverter", nextConverter)
                    .toString();
        }
    }

    public static Converter newIdentityConverter(String vendor, Format format) {
        return new IdentityConverter(vendor, format);
    }

    private final static class IdentityConverter
            implements Converter {

        private final String vendor;
        private final Format format;

        public IdentityConverter(String vendor, Format format) {
            this.vendor = checkNotNull(vendor);
            this.format = checkNotNull(format);
        }

        public String getVendor() {
            return vendor;
        }

        @Override
        public Format getSource() {
            return format;
        }

        @Override
        public Format getTarget() {
            return format;
        }

        @Override
        public Map<ID,ID> convert(Collection<ID> ids) {
            return Maps.uniqueIndex(ids, new Function<ID, ID>() {
                @Override
                public ID apply(ID input) {
                    return input;
                }
            });
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(vendor, format);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            IdentityConverter other = (IdentityConverter) obj;
            return Objects.equal(this.vendor, other.vendor) && Objects.equal(this.format, other.format);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("vendor", vendor)
                    .add("format", format)
                    .toString();
        }
    }
}
