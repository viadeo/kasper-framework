// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.id;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.id.ID;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;
import java.util.UUID;

import static com.viadeo.kasper.core.id.TestConverters.mockConverter;

@RunWith(MockitoJUnitRunner.class)
public class ConvertersUTest {

    @Mock
    private Converter UUIDToIdConverter;

    @Mock
    private Converter IdToStringConverter;

    private Converter converter;

    @Before
    public void setup() {
        UUIDToIdConverter = mockConverter("viadeo", TestFormats.UUID, TestFormats.ID);
        IdToStringConverter = mockConverter("viadeo", TestFormats.ID, TestFormats.STRING);

        converter = Converters.chain(UUIDToIdConverter, IdToStringConverter);
    }

    @Test
    public void convert_with_two_converters_and_missing_ids_must_return_incomplete_result() {
        // GIVEN
        ID firstUUID = new ID("viadeo", "member", TestFormats.UUID, UUID.randomUUID());
        ID firstID = new ID("viadeo", "member", TestFormats.ID, 42);
        ID firstString = new ID("viadeo", "member", TestFormats.STRING, String.valueOf(42));
        ID secondUUID = new ID("viadeo", "member", TestFormats.UUID, UUID.randomUUID());

        Map<ID, ID> firstConversionResult = Maps.newHashMap();
        firstConversionResult.put(firstUUID, firstID);
        Map<ID, ID> secondConversionResult = Maps.newHashMap();
        secondConversionResult.put(firstID, firstString);
        Mockito.when(UUIDToIdConverter.convert(Lists.newArrayList(firstUUID, secondUUID))).thenReturn(firstConversionResult);
        Mockito.when(IdToStringConverter.convert(firstConversionResult.values())).thenReturn(secondConversionResult);

        // WHEN
        Map<ID, ID> result = converter.convert(Lists.newArrayList(firstUUID, secondUUID));

        // THEN
        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(firstString, result.get(firstUUID));
    }

    @Test
    public void nominale_case_convert_with_two_converters_must_return_correct_results() {
        // GIVEN
        ID firstUUID = new ID("viadeo", "member", TestFormats.UUID, UUID.randomUUID());
        ID firstID = new ID("viadeo", "member", TestFormats.ID, 42);
        ID firstString = new ID("viadeo", "member", TestFormats.STRING, String.valueOf(42));
        ID secondUUID = new ID("viadeo", "member", TestFormats.UUID, UUID.randomUUID());
        ID secondID = new ID("viadeo", "member", TestFormats.ID, 43);
        ID secondString = new ID("viadeo", "member", TestFormats.STRING, String.valueOf(43));

        Map<ID, ID> firstConversionResult = Maps.newHashMap();
        firstConversionResult.put(firstUUID, firstID);
        firstConversionResult.put(secondUUID, secondID);
        Map<ID, ID> secondConversionResult = Maps.newHashMap();
        secondConversionResult.put(firstID, firstString);
        secondConversionResult.put(secondID, secondString);
        Mockito.when(UUIDToIdConverter.convert(Lists.newArrayList(firstUUID, secondUUID))).thenReturn(firstConversionResult);
        Mockito.when(IdToStringConverter.convert(firstConversionResult.values())).thenReturn(secondConversionResult);

        // WHEN
        Map<ID, ID> result = converter.convert(Lists.newArrayList(firstUUID, secondUUID));

        // THEN
        Assert.assertNotNull(result);
        Assert.assertEquals(2, result.size());
        Assert.assertEquals(firstString, result.get(firstUUID));
        Assert.assertEquals(secondString, result.get(secondUUID));
    }
}