package com.viadeo.kasper.exposition.http;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.viadeo.kasper.tools.ObjectMapperProvider;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

import static com.viadeo.kasper.exposition.http.HttpServletRequestToObject.StringRequestToObjectMapper2;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StringRequestToObjectMapper2UTest {

    public static class InputA {
        final String fieldA;
        final String fieldB;

        public InputA(String fieldA, String fieldB) {
            this.fieldA = fieldA;
            this.fieldB = fieldB;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fieldA, fieldB);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final InputA other = (InputA) obj;
            return Objects.equals(this.fieldA, other.fieldA) && Objects.equals(this.fieldB, other.fieldB);
        }
    }

    @Test
    public void map_fromStringFieldType_isOk() throws IOException {
        // Given
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterMap()).thenReturn(
                ImmutableMap.<String, String[]>builder()
                        .put("fieldA", new String[]{"foo"})
                        .put("fieldB", new String[]{"bar"})
                        .build()
        );

        final StringRequestToObjectMapper2 mapper = new StringRequestToObjectMapper2(
                ObjectMapperProvider.INSTANCE.mapper()
        );

        // When
        final InputA input = mapper.map(request, InputA.class);

        // Then
        assertNotNull(input);
        assertEquals("foo", input.fieldA);
        assertEquals("bar", input.fieldB);
    }

    // ------------------------------------------------------------------------

    public static class InputB {
        final String fieldA;
        final Integer fieldB;

        public InputB(String fieldA, Integer fieldB) {
            this.fieldA = fieldA;
            this.fieldB = fieldB;
        }
    }

    @Test
    public void map_fromStringFieldType_andIntegerFieldType_isOk() throws IOException {
        // Given
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterMap()).thenReturn(
                ImmutableMap.<String, String[]>builder()
                        .put("fieldA", new String[]{"foo"})
                        .put("fieldB", new String[]{"42"})
                        .build()
        );

        final StringRequestToObjectMapper2 mapper = new StringRequestToObjectMapper2(
                ObjectMapperProvider.INSTANCE.mapper()
        );

        // When
        final InputB input = mapper.map(request, InputB.class);

        // Then
        assertNotNull(input);
        assertEquals("foo", input.fieldA);
        assertEquals(42, (int) input.fieldB);
    }

    // ------------------------------------------------------------------------

    public static class InputC {
        final String fieldA;
        final InputA fieldB;

        public InputC(String fieldA, InputA fieldB) {
            this.fieldA = fieldA;
            this.fieldB = fieldB;
        }
    }

    @Test
    public void map_fromStringFieldType_andInputAFieldType_isOk() throws IOException {
        // Given
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterMap()).thenReturn(
                ImmutableMap.<String, String[]>builder()
                        .put("fieldA", new String[]{"foo"})
                        .put("fieldB.fieldA", new String[]{"boo"})
                        .put("fieldB.fieldB", new String[]{"far"})
                        .build()
        );

        final StringRequestToObjectMapper2 mapper = new StringRequestToObjectMapper2(
                ObjectMapperProvider.INSTANCE.mapper()
        );

        // When
        final InputC input = mapper.map(request, InputC.class);

        // Then
        assertNotNull(input);
        assertEquals("foo", input.fieldA);
        assertEquals("boo", input.fieldB.fieldA);
        assertEquals("far", input.fieldB.fieldB);
    }

    // ------------------------------------------------------------------------

    public static class InputD {
        final String[] fieldA;

        public InputD(String[] fieldA) {
            this.fieldA = fieldA;
        }
    }

    @Test
    public void map_fromArrayFieldType_withOnlyOneValue_isOk() throws IOException {
        // Given
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterMap()).thenReturn(
                ImmutableMap.<String, String[]>builder()
                        .put("fieldA", new String[]{"foo"})
                        .build()
        );

        final StringRequestToObjectMapper2 mapper = new StringRequestToObjectMapper2(
                ObjectMapperProvider.INSTANCE.mapper()
        );

        // When
        final InputD input = mapper.map(request, InputD.class);

        // Then
        assertNotNull(input);
        assertArrayEquals(new String[]{"foo"}, input.fieldA);
    }

    // ------------------------------------------------------------------------

    public static class InputE {
        final Collection fieldA;

        public InputE(Collection fieldA) {
            this.fieldA = fieldA;
        }
    }

    @Test
    public void map_fromCollectionFieldType_withOnlyOneValue_isOk() throws IOException {
        // Given
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterMap()).thenReturn(
                ImmutableMap.<String, String[]>builder()
                        .put("fieldA", new String[]{"foo"})
                        .build()
        );

        final StringRequestToObjectMapper2 mapper = new StringRequestToObjectMapper2(
                ObjectMapperProvider.INSTANCE.mapper()
        );

        // When
        final InputE input = mapper.map(request, InputE.class);

        // Then
        assertNotNull(input);
        assertEquals(Lists.newArrayList("foo"), input.fieldA);
    }

    // ------------------------------------------------------------------------

    public static class InputF {
        final Collection<InputA> fieldA;

        public InputF(Collection<InputA> fieldA) {
            this.fieldA = fieldA;
        }
    }

    @Test
    public void map_fromCollectionOfComplexFieldType_withOnlyOneValue_isOk() throws IOException {
        // Given
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterMap()).thenReturn(
                ImmutableMap.<String, String[]>builder()
                        .put("fieldA.fieldA", new String[]{"foo"})
                        .put("fieldA.fieldB", new String[]{"bar"})
                        .build()
        );

        final StringRequestToObjectMapper2 mapper = new StringRequestToObjectMapper2(
                ObjectMapperProvider.INSTANCE.mapper()
        );

        // When
        final InputF input = mapper.map(request, InputF.class);

        // Then
        assertNotNull(input);
        assertEquals(Lists.newArrayList(new InputA("foo", "bar")), input.fieldA);
    }

    @Test
    public void map_fromCollectionOfComplexFieldType_withTwoValues_isOk() throws IOException {
        // Given
        final HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameterMap()).thenReturn(
                ImmutableMap.<String, String[]>builder()
                        .put("fieldA._1.fieldA", new String[]{"foo1"})
                        .put("fieldA._1.fieldB", new String[]{"bar1"})
                        .put("fieldA._2.fieldA", new String[]{"foo2"})
                        .put("fieldA._2.fieldB", new String[]{"bar2"})
                        .build()
        );

        final StringRequestToObjectMapper2 mapper = new StringRequestToObjectMapper2(
                ObjectMapperProvider.INSTANCE.mapper()
        );

        // When
        final InputF input = mapper.map(request, InputF.class);

        // Then
        assertNotNull(input);
        assertEquals(Lists.newArrayList(new InputA("foo1", "bar1"), new InputA("foo2", "bar2")), input.fieldA);
    }
}
