package com.viadeo.kasper.context;

import com.google.common.base.Optional;
import org.junit.Test;

import static org.junit.Assert.*;

public class HttpContextHeadersUTest {

    @Test
    public void toPropertyKey_withUnknownProperty_returnAbsent() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromPropertyKey("fake");
        assertNotNull(optional);
        assertFalse(optional.isPresent());
    }

    @Test
    public void toPropertyKey_withKnownPropertyInLowerCase_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromPropertyKey("userlang");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

    @Test
    public void toPropertyKey_withKnownPropertyInUpperCase_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromPropertyKey("USERLANG");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

    @Test
    public void toPropertyKey_withKnownProperty_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromPropertyKey("userLang");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

    @Test
    public void toPropertyKey_withUnknownHeader_returnAbsent() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromHeader("X-KASPER-FAKE");
        assertNotNull(optional);
        assertFalse(optional.isPresent());
    }

    @Test
    public void toPropertyKey_withKnownHeaderInLowerCase_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromHeader("x-kasper-lang");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

    @Test
    public void toPropertyKey_withKnownHeaderInUpperCase_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromHeader("X-KASPER-LANG");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

    @Test
    public void toPropertyKey_withKnownHeader_isOk() {
        Optional<HttpContextHeaders> optional = HttpContextHeaders.fromHeader("X-KASPER-LANG");
        assertNotNull(optional);
        assertTrue(optional.isPresent());
        assertEquals(HttpContextHeaders.HEADER_USER_LANG, optional.get());
    }

}
