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
package com.viadeo.kasper.core.component.event.eventbus;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.serializer.*;

import java.io.IOException;
import java.io.InputStream;

import static com.google.common.base.Preconditions.checkNotNull;

public class JacksonSerializer implements Serializer {

    private final ObjectMapper objectMapper;
    private final ConverterFactory converterFactory;

    public JacksonSerializer(final ObjectMapper objectMapper) {
        this.objectMapper = checkNotNull(objectMapper);
        this.converterFactory = new ChainingConverterFactory();
    }

    @Override
    public <T> SerializedObject<T> serialize(final Object object, final Class<T> expectedRepresentation) {
        checkNotNull(object);
        checkNotNull(expectedRepresentation);

        try {
            final T convertedObject = convert(byte[].class, expectedRepresentation, objectMapper.writeValueAsBytes(object));
            return new SimpleSerializedObject<>(convertedObject, expectedRepresentation, typeForClass(object.getClass()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected <S, T> T convert(final Class<S> sourceType, final Class<T> targetType, final S source) {
        return getConverterFactory().getConverter(sourceType, targetType).convert(source);
    }

    @Override
    public <T> boolean canSerializeTo(final Class<T> expectedRepresentation) {
        return objectMapper.canSerialize(checkNotNull(expectedRepresentation));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <S, T> T deserialize(final SerializedObject<S> serializedObject) {
        checkNotNull(serializedObject);

        try {
            final InputStream serializedData = convert(serializedObject.getContentType(), InputStream.class, serializedObject.getData());
            final Class<?> clazz = Class.forName(serializedObject.getType().getName());
            return (T) objectMapper.readValue(serializedData, clazz);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class classForType(final SerializedType type) {
        try {
            return Class.forName(type.getName());
        } catch (ClassNotFoundException e) {
            throw new UnknownSerializedTypeException(type, e);
        }
    }

    @Override
    public SerializedType typeForClass(final Class type) {
        checkNotNull(type);
        return new SimpleSerializedType(type.getName(), "0");
    }

    @Override
    public ConverterFactory getConverterFactory() {
        return converterFactory;
    }
}