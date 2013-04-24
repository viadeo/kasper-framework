/*
 * Copyright 2013 Viadeo.com
 */

package com.viadeo.kasper.client;

import java.util.Date;
import javax.ws.rs.core.MultivaluedMap;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;
import com.viadeo.kasper.cqrs.query.IQuery;

public class KasperClientQueryCreationTest {

    private Date now = new Date();
    private DateTime dt = new DateTime();

    @Test(expected = KasperClientException.class)
    public void testThrowExceptionForComplexPojos() {
        KasperClient client = new KasperClient();
        client.prepareQueryParams(new ComplexQuery(new SomePojo("foo bar")));
    }

    @Test
    public void testComplexPojosWithCustomAdapter() {
        KasperClient client = new KasperClient.Builder().use(new TypeAdapter<SomePojo>() {
            @Override
            public void adapt(SomePojo value, QueryBuilder builder) {
                builder.addSingle("somePojo", 2);
                builder.addSingle("otherProperty", true);
            }
        }).create();
        assertEquals("2", client.prepareQueryParams(new ComplexQuery(new SomePojo("foo bar"))).getFirst("somePojo"));
        assertEquals("true", client.prepareQueryParams(new ComplexQuery(new SomePojo("foo bar"))).getFirst("otherProperty"));
    }

    @Test
    public void testPrepareQueryParamsWithCombinedQueries() {
        KasperClient client = new KasperClient();
        assertMoreComplexQueryContent(client.prepareQueryParams(new CombinedQuery(prepareMoreComplexQueryBean())));
    }

    @Test
    public void testPrepareQueryParamsWithComplexObject() {
        KasperClient client = new KasperClient();
        assertMoreComplexQueryContent(client.prepareQueryParams(prepareMoreComplexQueryBean()));
    }

    private void assertMoreComplexQueryContent(MultivaluedMap<String, String> queryParams) {
        assertEquals("foo", queryParams.getFirst("aStr"));
        assertEquals("1", queryParams.getFirst("aPrimitiveInt"));
        assertEquals("2", queryParams.getFirst("aInteger"));
        assertEquals("true", queryParams.getFirst("aPrimitiveBoolean"));
        assertEquals("" + now.getTime(), queryParams.getFirst("aDate"));
        assertEquals("" + dt.getMillis(), queryParams.get("multipleDates").get(0));
        assertEquals("" + dt.getMillis(), queryParams.get("multipleDates").get(0));
    }

    private MoreComplexQuery prepareMoreComplexQueryBean() {
        return new MoreComplexQuery(
                "foo",
                1,
                new Integer(2),
                true,
                now,
                new DateTime[] { dt, dt });
    }

    class CombinedQuery implements IQuery {
        private static final long serialVersionUID = 7214975580153615343L;
        private final MoreComplexQuery innerQuery;

        public CombinedQuery(MoreComplexQuery innerQuery) {
            this.innerQuery = innerQuery;
        }

        public MoreComplexQuery getInnerQuery() {
            return this.innerQuery;
        }
    }

    class ComplexQuery implements IQuery {
        private static final long serialVersionUID = -5681649437512129142L;
        private final SomePojo complexPojo;

        public ComplexQuery(SomePojo complexPojo) {
            this.complexPojo = complexPojo;
        }

        public SomePojo getComplexPojo() {
            return this.complexPojo;
        }
    }

    class SomePojo {
        private final String value;

        public SomePojo(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    class MoreComplexQuery implements IQuery {
        private static final long serialVersionUID = -1376553638435430921L;

        private final String aStr;
        private final int aPrimitiveInt;
        private final Integer aInteger;
        private final boolean aPrimitiveBoolean;
        private final Date aDate;
        private final DateTime[] multipleDates;

        public MoreComplexQuery(String aStr, int aPrimitiveInt, Integer aInteger, boolean aPrimitiveBoolean, Date aDate, DateTime[] multipleDates) {
            super();
            this.aStr = aStr;
            this.aPrimitiveInt = aPrimitiveInt;
            this.aInteger = aInteger;
            this.aPrimitiveBoolean = aPrimitiveBoolean;
            this.aDate = aDate;
            this.multipleDates = multipleDates;
        }

        public String getaStr() {
            return this.aStr;
        }

        public int getaPrimitiveInt() {
            return this.aPrimitiveInt;
        }

        public Integer getaInteger() {
            return this.aInteger;
        }

        public boolean isaPrimitiveBoolean() {
            return this.aPrimitiveBoolean;
        }

        public Date getaDate() {
            return this.aDate;
        }

        public DateTime[] getMultipleDates() {
            return this.multipleDates;
        }
    }
}
