// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Map;

import static com.viadeo.kasper.exposition.http.StringRequestToObjectMapper2UTest.*;
import static org.junit.Assert.*;

public class JsonTransformerUTest {

    @Test
    public void isArrayOrCollection_withoutSpecifiedClass_returnFalse() {
        // Given nothing

        // When
        final boolean arrayOrCollection = JsonTransformer.isArrayOrCollection("foo", Optional.<Class>absent());

        // Then
        assertFalse(arrayOrCollection);
    }

    @Test
    public void isArrayOrCollection_withSpecifiedClass_withUnknownFieldName_returnFalse() {
        // Given nothing

        // When
        final boolean arrayOrCollection = JsonTransformer.isArrayOrCollection("foo", Optional.<Class>of(InputA.class));

        // Then
        assertFalse(arrayOrCollection);
    }

    @Test
    public void isArrayOrCollection_withSpecifiedClass_withFieldName_referencingAString_returnFalse() {
        // Given nothing

        // When
        final boolean arrayOrCollection = JsonTransformer.isArrayOrCollection("fieldsA", Optional.<Class>of(InputA.class));

        // Then
        assertFalse(arrayOrCollection);
    }

    @Test
    public void isArrayOrCollection_withSpecifiedClass_withFieldName_referencingAnArray_returnTrue() {
        // Given nothing

        // When
        final boolean arrayOrCollection = JsonTransformer.isArrayOrCollection("fieldA", Optional.<Class>of(InputD.class));

        // Then
        assertTrue(arrayOrCollection);
    }

    @Test
    public void isArrayOrCollection_withSpecifiedClass_withFieldName_referencingACollection_returnTrue() {
        // Given nothing

        // When
        final boolean arrayOrCollection = JsonTransformer.isArrayOrCollection("fieldA", Optional.<Class>of(InputF.class));

        // Then
        assertTrue(arrayOrCollection);
    }

    @Test
    public void getSubClass_withoutSpecifiedClass_returnAbsent() {
        // Given nothing

        // When
        final Optional<Class> optionalClass = JsonTransformer.getSubClass("foo", Optional.<Class>absent());

        // Then
        assertFalse(optionalClass.isPresent());
    }

    @Test
    public void getSubClass_withSpecifiedClass_withUnknownFieldName_returnAbsent() {
        // Given nothing

        // When
        final Optional<Class> optionalClass = JsonTransformer.getSubClass("foo", Optional.<Class>of(InputA.class));

        // Then
        assertFalse(optionalClass.isPresent());
    }

    @Test
    public void getSubClass_withSpecifiedClass_withFieldName_referencingAString_returnClass() {
        // Given nothing

        // When
        final Optional<Class> optionalClass = JsonTransformer.getSubClass("fieldA", Optional.<Class>of(InputA.class));

        // Then
        assertTrue(optionalClass.isPresent());
        assertEquals(String.class, optionalClass.get());
    }

    @Test
    public void getSubClass_withSpecifiedClass_withFieldName_referencingAnArray_returnClass() {
        // Given nothing

        // When
        final Optional<Class> optionalClass = JsonTransformer.getSubClass("fieldA", Optional.<Class>of(InputD.class));

        // Then
        assertTrue(optionalClass.isPresent());
        assertEquals(String[].class, optionalClass.get());
    }

    @Test
    public void getSubClass_withSpecifiedClass_withFieldName_referencingACollection_returnClass() {
        // Given nothing

        // When
        final Optional<Class> optionalClass = JsonTransformer.getSubClass("fieldA", Optional.<Class>of(InputF.class));

        // Then
        assertTrue(optionalClass.isPresent());
        assertEquals(InputA.class, optionalClass.get());
    }

    @Test
    public void toJson_fromOneParameter_isOk() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("foo", new String[]{"value"})
                .build();

        // When
        final String json = JsonTransformer.from(maps).toJson();

        // Then
        assertNotNull(json);
        assertEquals("{\"foo\":\"value\"}", json);
    }

    @Test
    public void toJson_withCorrectExpectedType_fromOneParameter_isOk() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("field", new String[]{"value"})
                .build();

        // When
        final String json = JsonTransformer.from(ObjectA.class, maps).toJson();

        // Then
        assertNotNull(json);
        assertEquals("{\"field\":\"value\"}", json);
    }

    @Test
    public void toJson_withBadFieldType_fromOneParameter_isOk() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("field2", new String[]{"value"})
                .build();

        // When
        final String json = JsonTransformer.from(ObjectA.class, maps).toJson();

        // Then
        assertNotNull(json);
        assertEquals("{\"field2\":\"value\"}", json);
    }

    @Test
    public void toJson_fromTwoIdenticalParameters_isOk() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("foo", new String[]{"value1", "value2"})
                .build();

        // When
        final String json = JsonTransformer.from(maps).toJson();

        // Then
        assertNotNull(json);
        assertEquals("{\"foo\":[\"value1\",\"value2\"]}", json);
    }

    @Test
    public void toJson_fromTwoIdenticalParametersWithSameValue_isOk() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("foo", new String[]{"value1", "value1"})
                .build();

        // When
        final String json = JsonTransformer.from(maps).toJson();

        // Then
        assertNotNull(json);
        assertEquals("{\"foo\":[\"value1\",\"value1\"]}", json);
    }

    @Test
    public void toJson_fromTwoDistinctParameters_isOk() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("foo", new String[]{"value1", "value2"})
                .put("bar", new String[]{"valueA"})
                .build();

        // When
        final String json = JsonTransformer.from(maps).toJson();

        // Then
        assertNotNull(json);
        assertEquals("{\"foo\":[\"value1\",\"value2\"],\"bar\":\"valueA\"}", json);
    }

    @Test
    public void toJson_fromOneComplexParameter_isOk() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("foo.bar", new String[]{"value"})
                .build();

        // When
        final String json = JsonTransformer.from(maps).toJson();

        // Then
        assertNotNull(json);
        assertEquals("{\"foo\":{\"bar\":\"value\"}}", json);
    }

    @Test
    public void toJson_fromTwoIdenticalComplexParameter_isOk() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("foo.bar", new String[]{"value1", "value2"})
                .build();

        // When
        final String json = JsonTransformer.from(maps).toJson();

        // Then
        assertNotNull(json);
        assertEquals("{\"foo\":{\"bar\":[\"value1\",\"value2\"]}}", json);
    }

    @Test
    public void toJson_fromTwoDistinctComplexParameter_isOk() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("foo.bar", new String[]{"value1", "value2"})
                .put("foo.oo", new String[]{"valueA"})
                .build();

        // When
        final String json = JsonTransformer.from(maps).toJson();

        // Then
        assertNotNull(json);
        assertEquals("{\"foo\":{\"oo\":\"valueA\",\"bar\":[\"value1\",\"value2\"]}}", json);
    }

    @Test(expected = JsonTransformer.JsonTransformerException.class)
    public void toJson_fromTwoDistinctComplexParameter_throwException() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("foo.bar", new String[]{"value1", "value2"})
                .put("foo", new String[]{"valueA"})
                .build();

        // When
        JsonTransformer.from(maps).toJson();

        // Then throw an exception
    }

    @Test
    public void toJson_fromParameterWithoutSpecifiedValue_isOk() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("foo.bar", new String[]{"value1", "value2"})
                .put("foo.oo", new String[]{})
                .build();

        // When
        final String json = JsonTransformer.from(maps).toJson();

        // Then
        assertNotNull(json);
        assertEquals("{\"foo\":{\"bar\":[\"value1\",\"value2\"]}}", json);
    }

    @Test
    public void toJson_fromParameterWithoutSpecifiedValue_isOk_2() {
        // Given
        final Map<String,String[]> maps = ImmutableMap.<String,String[]>builder()
                .put("foo.oo", new String[]{})
                .build();

        // When
        final String json = JsonTransformer.from(maps).toJson();

        // Then
        assertNotNull(json);
        assertEquals("{}", json);
    }

    public static class ObjectA {
        @SuppressWarnings("unused")
        public String field;
    }

}
