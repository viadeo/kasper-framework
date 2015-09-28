// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.context;

import com.google.common.collect.ImmutableMap;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.id.Format;
import com.viadeo.kasper.api.id.FormatAdapter;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.SimpleIDBuilder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class DefaultContextHelperUTest {

    private static final Format DB_ID = new FormatAdapter("db-id", Integer.class) {
        @SuppressWarnings("unchecked")
        @Override
        public <E> E parseIdentifier(String identifier) {
            return (E) new Integer(identifier);
        }
    };

    private DefaultContextHelper contextHelper;

    @Before
    public void setUp() throws Exception {
        contextHelper = new DefaultContextHelper(new SimpleIDBuilder(DB_ID));
    }

    @Test
    public void toContext_withBasicProperty_isOk() {
        // Given
        Context givenContext = Contexts.builder().withFunnelVersion("funnelVr").build();

        // When
        Context actualContext = contextHelper.createFrom(givenContext.asMap());

        // Then
        Assert.assertNotNull(actualContext);
        Assert.assertEquals("funnelVr", actualContext.getFunnelVersion().orNull());
    }

    @Test
    public void toContext_withCustomProperty_isOk() {
        // Given
        Context givenContext = Contexts.builder().with("miaou", "hello kitty!").build();

        // When
        Context actualContext = contextHelper.createFrom(givenContext.asMap());

        // Then
        Assert.assertNotNull(actualContext);
        Assert.assertTrue(actualContext.getProperties().containsKey("miaou"));
        Assert.assertEquals("hello kitty!", actualContext.getProperty("miaou").get());
    }

    @Test
    public void toContext_withEmptyUserID_isOk() {
        // When
        Context actualContext = contextHelper.createFrom(ImmutableMap.<String, String>builder().put(Context.USER_ID_SHORTNAME, "").build());

        // Then
        Assert.assertNotNull(actualContext);
        Assert.assertFalse(actualContext.getUserID().isPresent());
    }

    @Test
    public void toContext_withUserID_isOk() {
        // Given
        Context givenContext = Contexts.builder()
                .withUserId("4")
                .withUserID(new ID("viadeo", "member", DB_ID, 4))
                .withSecurityToken("003chh8bxkrn338")
                .build();

        // When
        Context actualContext = contextHelper.createFrom(givenContext.asMap());

        // Then
        Assert.assertNotNull(actualContext);
        Assert.assertEquals(givenContext.getSecurityToken(), actualContext.getSecurityToken());
        Assert.assertEquals(givenContext.getUserId(), actualContext.getUserId());
        Assert.assertEquals(givenContext.getUserID(), actualContext.getUserID());
    }
}
