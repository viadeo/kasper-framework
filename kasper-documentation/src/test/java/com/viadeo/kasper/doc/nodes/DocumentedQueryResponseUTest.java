// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.doc.nodes;

import com.viadeo.kasper.domain.sample.root.api.query.GetAllMembersQueryHandler;
import com.viadeo.kasper.domain.sample.root.api.query.GetMembersQueryHandler;
import org.junit.Test;

import static org.junit.Assert.*;

public class DocumentedQueryResponseUTest {

    @Test
    public void init_withQueryResult_shouldBeDetailed() {
        // When
        final DocumentedBean bean = new DocumentedQueryResponse(GetMembersQueryHandler.MembersResult.class);

        // Then
        assertNotNull(bean);
        assertEquals(3, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("result", prop.getName());
        assertEquals("MembersResult", prop.getType());
        assertFalse(prop.isList());
        assertFalse(prop.getLinkedConcept());
        assertTrue(prop.isQueryResult());
    }

    @Test
    public void init_withCollectionQueryResult_shouldBeDetailed() {
        // When
        final DocumentedBean bean = new DocumentedQueryResponse(GetAllMembersQueryHandler.AllMembersResult.class);

        // Then
        assertNotNull(bean);
        assertEquals(3, bean.size());

        final DocumentedProperty prop = bean.get(0);
        assertEquals("result", prop.getName());
        assertEquals("AllMembersResult", prop.getType());
        assertEquals("MembersResult", prop.getElemType());
        assertFalse(prop.isList());
        assertFalse(prop.getLinkedConcept());
        assertTrue(prop.isQueryResult());
    }
}
