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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.id.ID;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.UUID;

import static com.viadeo.kasper.core.id.TestConverters.mockConverter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(firstString, result.get(firstUUID));
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
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(firstString, result.get(firstUUID));
        assertEquals(secondString, result.get(secondUUID));
    }
}