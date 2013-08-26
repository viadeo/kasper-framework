// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.query.exposition.scala;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.scala.DefaultScalaModule;
import com.viadeo.kasper.tools.ObjectMapperProvider;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class T {

    /**
     * @param args
     * @throws IOException 
     * @throws JsonMappingException 
     * @throws JsonParseException 
     */
    public static void main(final String[] args) throws JsonParseException, JsonMappingException, IOException {
        final ObjectMapper mapper = ObjectMapperProvider.instance.mapper().registerModule(new DefaultScalaModule());
        final Map m = mapper.readValue("{\"list\": []}", Map.class);

        System.out.println(mapper.readValue("[]", List.class).getClass());
        System.out.println(mapper.readValue("{\"list\": []}", Pojo.class).list.getClass());
        System.out.println(m.get("list").getClass());
    }

    
    static class Pojo {
        public List list;
    }

}
