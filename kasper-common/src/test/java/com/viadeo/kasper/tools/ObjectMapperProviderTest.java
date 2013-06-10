package com.viadeo.kasper.tools;

import com.fasterxml.jackson.databind.ObjectReader;
import com.viadeo.kasper.cqrs.query.IQueryDTO;
import com.viadeo.kasper.cqrs.query.impl.AbstractQueryCollectionDTO;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class ObjectMapperProviderTest {
    @Test public void dontFailOnUnknownProperty() throws IOException {
        final SomeCollectionDTO dto = new SomeCollectionDTO();
        dto.setList(Arrays.asList(new SomeDTO(), new SomeDTO()));
        
        final String json = ObjectMapperProvider.instance.objectWriter().writeValueAsString(dto);
        final ObjectReader objectReader = ObjectMapperProvider.instance.objectReader();
        final SomeCollectionDTO actual = objectReader.readValue(objectReader.getFactory().createJsonParser(json), SomeCollectionDTO.class);
        
        assertEquals(dto.getCount(), actual.getCount());
    }
    
    static class SomeDTO implements IQueryDTO {
        private static final long serialVersionUID = -3621610243017076348L;

        public String getStr() {
            return "str";
        }
    }
    
    static class SomeCollectionDTO extends AbstractQueryCollectionDTO<SomeDTO> {
        private static final long serialVersionUID = 8849846911146025322L;
        
    }
}
