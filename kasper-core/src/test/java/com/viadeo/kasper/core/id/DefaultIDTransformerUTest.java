// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.viadeo.kasper.api.id.ID;
import com.viadeo.kasper.api.id.IDBuilder;
import com.viadeo.kasper.api.id.IDTransformer;
import com.viadeo.kasper.api.id.SimpleIDBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.viadeo.kasper.core.id.TestConverters.mockConverter;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

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

    @Test(expected = FailedToTransformIDException.class)
    public void to_withOneID_withFormat_withConverterVendorMismatch_throwException() {
        // Given
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);

        Converter converter = mockConverter("glinglin", TestFormats.ID, TestFormats.UUID);
        when(converter.convert(ArgumentMatchers.<ID>anyCollection()))
                .thenThrow(new AssertionError("unexpected call"));
        converterRegistry.register(converter);

        // When
        transformer.to(TestFormats.UUID, id);

        // Then throws exception
    }

    @Test(expected = FailedToTransformIDException.class)
    public void to_withOneID_withFormat_withConverterInputFormatMismatch_throwException() {
        // Given
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);

        Converter converter = mockConverter("viadeo", TestFormats.UUID, TestFormats.UUID);
        when(converter.convert(ArgumentMatchers.<ID>anyCollection()))
                .thenThrow(new AssertionError("unexpected call"));
        converterRegistry.register(converter);

        // When
        transformer.to(TestFormats.UUID, id);

        // Then throws exception
    }

    @Test(expected = FailedToTransformIDException.class)
    public void to_withOneID_withFormat_withConverterOutputFormatMismatch_throwException() {
        // Given
        ID id = new ID("viadeo", "member", TestFormats.ID, 42);

        Converter converter = mockConverter("viadeo", TestFormats.ID, TestFormats.ID);
        when(converter.convert(ArgumentMatchers.<ID>anyCollection()))
                .thenThrow(new AssertionError("unexpected call"));
        converterRegistry.register(converter);

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
        when(converter.convert(ArgumentMatchers.<ID>anyCollection())).thenReturn(ImmutableMap.<ID, ID>builder().put(id, uuid).build());
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
        doThrow(new RuntimeException("Fake exception"))
                .when(mockedConverter).convert(ArgumentMatchers.<ID>anyCollection());

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
        doThrow(new FailedToTransformIDException("Fake exception"))
                .when(mockedConverter).convert(ArgumentMatchers.<ID>anyCollection());

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
        when(converter.convert(ArgumentMatchers.<ID>anyCollection())).thenReturn(ImmutableMap.<ID, ID>builder().put(id1, uuid1).put(id2, uuid2).build());
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

    @Test
    public void to_withMultiId_containingNotTheSameFormat_returnMultiId() {
        // Given
        ID id1 = new ID("viadeo", "member", TestFormats.ID, 42);
        ID uuid1 = new ID("viadeo", "member", TestFormats.UUID, UUID.randomUUID());

        ID id2 = new ID("viadeo", "member", TestFormats.UUID, UUID.randomUUID());

        Converter converter = mockConverter("viadeo", TestFormats.ID, TestFormats.UUID);
        when(converter.convert(ArgumentMatchers.<ID>anyCollection())).thenReturn(ImmutableMap.of(
                id1, uuid1
        ));
        converterRegistry.register(converter);

        // When
        Map<ID, ID> ids = transformer.to(TestFormats.UUID, id1, id2);

        // Then
        assertNotNull(ids);
        assertEquals(2, ids.size());

        assertSame(uuid1, ids.get(id1));
        assertSame(id2, ids.get(id2));
    }

    @Test
    public void to_withMultiId_containingNotTheSameVendor_returnMultiId() {
        // Given
        ID id1 = new ID("viadeo", "member", TestFormats.ID, 42);
        ID uuid1 = new ID("viadeo", "member", TestFormats.UUID, UUID.randomUUID());

        ID id2 = new ID("glinglin", "member", TestFormats.ID, 43);
        ID uuid2 = new ID("glinglin", "member", TestFormats.UUID, UUID.randomUUID());

        Converter converter1 = mockConverter("viadeo", TestFormats.ID, TestFormats.UUID);
        when(converter1.convert(ArgumentMatchers.<ID>anyCollection())).thenReturn(ImmutableMap.of(
                id1, uuid1
        ));
        converterRegistry.register(converter1);

        Converter converter2 = mockConverter("glinglin", TestFormats.ID, TestFormats.UUID);
        when(converter2.convert(ArgumentMatchers.<ID>anyCollection())).thenReturn(ImmutableMap.of(
                id2, uuid2
        ));
        converterRegistry.register(converter2);

        // When
        Map<ID, ID> ids = transformer.to(TestFormats.UUID, id1, id2);

        // Then
        assertNotNull(ids);
        assertEquals(2, ids.size());

        assertSame(uuid1, ids.get(id1));
        assertSame(uuid2, ids.get(id2));
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
