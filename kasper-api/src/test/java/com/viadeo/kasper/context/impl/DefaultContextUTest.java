// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.context.impl;

import com.google.common.collect.Maps;
import com.viadeo.kasper.context.Context;
import org.junit.Test;

import java.util.HashMap;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class DefaultContextUTest {

    @Test
    public void testChild() {
        // Given
        final DefaultContext context = new DefaultContext();

        // When
        final DefaultContext newContext = context.child();

        // When
        assertEquals(context.getRequestCorrelationId(), newContext.getRequestCorrelationId());
        assertEquals(context.getSessionCorrelationId(), newContext.getSessionCorrelationId());
        assertEquals(context.getApplicationId(), newContext.getApplicationId());
        assertEquals(context.getUserId(), newContext.getUserId());
        assertEquals(context.getUserLang(), newContext.getUserLang());
        assertEquals(context.getKasperCorrelationId(), newContext.getKasperCorrelationId());
        assertEquals(context.getProperties().size(), newContext.getProperties().size());
        assertEquals(context.getSequenceIncrement() + 1, newContext.getSequenceIncrement());
        assertEquals(context, newContext);
    }

    @Test
    public void testUserLangAsLocale_with_en() {

        // Given
        final DefaultContext context = new DefaultContext();
        context.setUserLang("en");

        // When
        final Locale locale = context.getUserLangAsLocale();

        // Then
        assertEquals("en", locale.getLanguage());
        assertEquals("", locale.getVariant());
        assertEquals("English", locale.getDisplayLanguage());
        assertEquals("English", locale.getDisplayName());
        assertEquals("", locale.getDisplayVariant());
        assertEquals("en", locale.toLanguageTag());
        assertEquals("", locale.getCountry());
    }

    @Test
    public void testUserLangAsLocale_with_en_US() {

        // Given
        final DefaultContext context = new DefaultContext();
        context.setUserLang("en-us");

        // When
        final Locale locale = context.getUserLangAsLocale();

        // Then
        assertEquals("en", locale.getLanguage());
        assertEquals("", locale.getVariant());
        assertEquals("English", locale.getDisplayLanguage());
        assertEquals("English (United States)", locale.getDisplayName());
        assertEquals("", locale.getDisplayVariant());
        assertEquals("en-US", locale.toLanguageTag());
        assertEquals("US", locale.getCountry());
    }

    @Test
    public void testUserLangAsLocale_with_wrong() {

        // Given
        final DefaultContext context = new DefaultContext();
        context.setUserLang("wrong");

        // When
        final Locale locale = context.getUserLangAsLocale();

        // Then
        assertEquals("wrong", locale.getLanguage());
        assertEquals("", locale.getVariant());
        assertEquals("wrong", locale.getDisplayLanguage());
        assertEquals("wrong", locale.getDisplayName());
        assertEquals("", locale.getDisplayVariant());
        assertEquals("wrong", locale.toLanguageTag());
        assertEquals("", locale.getCountry());
    }

    @Test
    public void init_fromEmptyMap_iOk() {
        // Given
        final HashMap<String, String> map = Maps.newHashMap();

        // When
        final DefaultContext context = new DefaultContext(map);

        // Then
        assertEquals(Context.DEFAULT_USER_ID, context.getUserId());
        assertEquals(Context.DEFAULT_USER_LANG, context.getUserLang());
        assertEquals(Context.DEFAULT_USER_COUNTRY, context.getUserCountry());
        assertEquals(Context.DEFAULT_REQCORR_ID, context.getRequestCorrelationId());
        assertEquals(Context.DEFAULT_FUNCORR_ID, context.getFunnelCorrelationId());
        assertEquals(Context.DEFAULT_SESSCORR_ID, context.getSessionCorrelationId());
        assertEquals(Context.DEFAULT_APPLICATION_ID, context.getApplicationId());
        assertEquals(Context.DEFAULT_SECURITY_TOKEN, context.getSecurityToken());
        assertEquals(Context.DEFAULT_FUNNEL_NAME, context.getFunnelName());
        assertEquals(Context.DEFAULT_FUNNEL_VERSION, context.getFunnelVersion());
        assertEquals(Context.DEFAULT_IP_ADDRESS, context.getIpAddress());
    }

    @Test
    public void init_fromMap_iOk() {
        // Given
        final HashMap<String, String> map = Maps.newHashMap();
        map.put(Context.UID_SHORTNAME, "foobar");
        map.put(Context.ULANG_SHORTNAME, "ooL");
        map.put(Context.UCOUNTRY_SHORTNAME, "oo");
        map.put(Context.REQUEST_CID_SHORTNAME, "requestCID");
        map.put(Context.FUNNEL_CID_SHORTNAME, "funnelCID");
        map.put(Context.SESSION_CID_SHORTNAME, "sessionCID");
        map.put(Context.APPLICATION_ID_SHORTNAME, "applicationId");
        map.put(Context.SECURITY_TOKEN_SHORTNAME, "security");
        map.put(Context.FUNNEL_NAME_SHORTNAME, "funnelName");
        map.put(Context.FUNNEL_VERS_SHORTNAME, "funnelVersion");
        map.put(Context.IP_ADDRESS_SHORTNAME, "192.168.0.1");

        // When
        final DefaultContext context = new DefaultContext(map);

        // Then
        assertEquals("foobar", context.getUserId());
        assertEquals("ooL", context.getUserLang());
        assertEquals("oo", context.getUserCountry());
        assertEquals("requestCID", context.getRequestCorrelationId());
        assertEquals("funnelCID", context.getFunnelCorrelationId());
        assertEquals("sessionCID", context.getSessionCorrelationId());
        assertEquals("applicationId", context.getApplicationId());
        assertEquals("security", context.getSecurityToken());
        assertEquals("funnelName", context.getFunnelName());
        assertEquals("funnelVersion", context.getFunnelVersion());
        assertEquals("192.168.0.1", context.getIpAddress());
    }

}
