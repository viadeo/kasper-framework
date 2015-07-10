// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.api.domain.response;

import com.viadeo.kasper.api.domain.response.CoreReasonCode;
import com.viadeo.kasper.api.domain.response.KasperReason;
import org.junit.Test;

public class KasperReasonUTest {

    @Test
    public void init_withExceptionWithoutMessage_isOk() {
        new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, new IllegalArgumentException());
    }
}
