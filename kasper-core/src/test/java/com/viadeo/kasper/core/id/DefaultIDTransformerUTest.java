// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.api.id.IDTransformer;
import com.viadeo.kasper.api.id.SimpleIDBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.viadeo.kasper.core.id.TestConverters.mockConverter;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultIDTransformerUTest {

    private ConverterRegistry converterRegistry;
    private DefaultIDTransformer transformer;
    private IDBuilder idBuilder;

    @Before
    public void setUp() throws Exception {
        converterRegistry = new ConverterRegistry();
        transformer = new DefaultIDTransformer(converterRegistry);
        idBuilder = new SimpleIDBuilder(TestFormats.UUID, TestFormats.ID);
    }

    @Test
    public void to_withOneID_withSameFormatThanSpecifiedId_isOk() throws Exception {
        // Given
        ID givenId = idBuilder.build("urn:viadeo:member:id:42");

        // When
        ID actualId = transformer.to(TestFormats.ID, givenId);

        // Then
        assertNotNull(actualId);
        assertTrue(givenId == actualId);
    }

    @Test(expected = NullPointerException.class)
    public void to_withOneID_withNullAsFormat_throwException() throws Exception {
        transformer.to(null, new ID("viadeo", "member", TestFormats.ID, 42));
    }

    @Test(expected = NullPointerException.class)
    public void to_withOneID_withNullAsId_throwException() throws Exception {
        transformer.to(TestFormats.ID, (ID) null);
    }

    @Test(expected = FailedToTransformIDException.class)
    public void to_withOneID_withFormat_withoutConverter_throwException() throws Exception {
        // Given
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);

        // When
        transformer.to(TestFormats.UUID, id);

        // Then throws exception
    }

    @Test
    public void to_withOneID_withFormat_withConverter_returnId() throws Exception {
        // Given
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);
        ID uuid = new ID("viadeo", "member", TestFormats.UUID, UUID.randomUUID());

        Converter converter = mockConverter("viadeo", TestFormats.ID, TestFormats.UUID);
        when(converter.convert(anyCollectionOf(ID.class))).thenReturn(ImmutableMap.<ID, ID>builder().put(id, uuid).build());
        converterRegistry.register(converter);

        // When
        ID actualId = transformer.to(TestFormats.UUID, id);

        // Then
        assertNotNull(actualId);
    }

    @Test(expected = FailedToTransformIDException.class)
    public void to_withFormat_withConverter_withUnexpectedRuntimeException_throwException() throws Exception {
        // Given
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);

        Converter mockedConverter = mock(Converter.class);
        when(mockedConverter.getSource()).thenReturn(TestFormats.ID);
        when(mockedConverter.getTarget()).thenReturn(TestFormats.UUID);
        when(mockedConverter.getVendor()).thenReturn("viadeo");
        Mockito.doThrow(new RuntimeException("Fake exception")).when(mockedConverter).convert(Sets.newHashSet(id));

        converterRegistry.register(mockedConverter);

        IDTransformer transformer = new DefaultIDTransformer(converterRegistry);

        // When
        transformer.to(TestFormats.UUID, id);

        // Then throws exception
    }

    @Test(expected = FailedToTransformIDException.class)
    public void to_withFormat_withConverter_withFailedToTransformIDException_throwException() throws Exception {
        // Given
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);

        Converter mockedConverter = mock(Converter.class);
        when(mockedConverter.getSource()).thenReturn(TestFormats.ID);
        when(mockedConverter.getTarget()).thenReturn(TestFormats.UUID);
        when(mockedConverter.getVendor()).thenReturn("viadeo");
        Mockito.doThrow(new FailedToTransformIDException("Fake exception")).when(mockedConverter).convert(Sets.newHashSet(id));

        converterRegistry.register(mockedConverter);

        IDTransformer transformer = new DefaultIDTransformer(converterRegistry);

        // When
        transformer.to(TestFormats.UUID, id);

        // Then throws exception
    }

    @Test
    public void to_withMultiId_withFormat_withConverter_returnMultiId() {
        // Given
        ID id1 = new ID("viadeo", "member", TestFormats.ID, 42);
        ID uuid1 = new ID("viadeo", "member", TestFormats.UUID, UUID.randomUUID());

        ID id2 = new ID("viadeo", "member", TestFormats.ID, 43);
        ID uuid2 = new ID("viadeo", "member", TestFormats.UUID, UUID.randomUUID());

        Converter converter = mockConverter("viadeo", TestFormats.ID, TestFormats.UUID);
        when(converter.convert(anyCollectionOf(ID.class))).thenReturn(ImmutableMap.<ID, ID>builder().put(id1, uuid1).put(id2, uuid2).build());
        converterRegistry.register(converter);

        // When
        Map<ID,ID> ids = transformer.to(TestFormats.UUID, id1, id2);

        // Then
        assertNotNull(ids);
        assertEquals(2, ids.size());

        assertEquals(uuid1.getIdentifier(), ids.get(id1).getIdentifier());
        assertEquals(uuid2.getIdentifier(), ids.get(id2).getIdentifier());
    }

    @Test(expected = IllegalArgumentException.class)
    public void to_withMultiId_containingNullAsId_throwException() {
        // Given
        ID id1 = new ID("viadeo", "member", TestFormats.ID, 42);

        // When
        transformer.to(TestFormats.UUID, Lists.newArrayList(id1, null));

        // Then throws exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void to_withMultiId_containingNotTheSameFormat_throwException() {
        // Given
        ID id1 = new ID("viadeo", "member", TestFormats.ID, 42);
        ID id2 = new ID("viadeo", "member", TestFormats.UUID, UUID.randomUUID());

        // When
        transformer.to(TestFormats.UUID, id1, id2);

        // Then throws exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void to_withMultiId_containingNotTheSameVendor_throwException() {
        // Given
        ID id1 = new ID("viadeo", "member", TestFormats.ID, 42);
        ID id2 = new ID("glinglin", "member", TestFormats.ID, 43);

        // When
        transformer.to(TestFormats.UUID, id1, id2);

        // Then throws exception
    }

    @Test
    public void to_withMultiId_containingNoElements_isOk() {
        // Given nothing

        // When
        Map<ID,ID> convertedIds = transformer.to(TestFormats.UUID, Lists.<ID>newArrayList());

        // Then
        assertNotNull(convertedIds);
        assertEquals(0, convertedIds.size());
    }

    @Test
    public void accept_withAdaptedConverter_returnTrue() throws Exception {
        // Given
        Converter converter = new AbstractSimpleConverter("viadeo", TestFormats.ID, TestFormats.UUID) {
            @Override
            public ID convert(ID id) { return id; }
        };

        // When
        boolean accept = transformer.accept(converter, "viadeo", TestFormats.ID, TestFormats.UUID);

        // Then
        assertTrue(accept);
    }

    @Test
    public void accept_withUnadaptedConverter_returnFalse() throws Exception {
        // Given
        Converter Converter = new AbstractSimpleConverter("viadeo", TestFormats.ID, TestFormats.UUID) {
            @Override
            public ID convert(ID id) { return id; }
        };

        // When
        boolean accept = transformer.accept(Converter, "viadeo", TestFormats.ID, TestFormats.STRING);

        // Then
        assertFalse(accept);
    }

    @Test
    public void to_withDuplicateIDs_isOk() {
        // Given
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);
        List<ID> ids = Lists.newArrayList(id, id);

        // When
        Map<ID, ID> map = transformer.to(TestFormats.ID, ids);

        // Then
        assertNotNull(map);
        assertEquals(1, map.size());
        assertEquals(id, map.get(id));
    }

}
