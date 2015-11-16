// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.id;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class UUIDFormatUTest {

    @Test
    public void parseIdentifier_fromAStringifyUUID_isOk() {
        // Given
        UUID givenUuid = UUID.randomUUID();

        // When
        UUID actualUuid = new UUIDFormat().parseIdentifier(givenUuid.toString());

        // Then
        Assert.assertEquals(givenUuid, actualUuid);
    }
}
