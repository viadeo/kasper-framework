// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.test.platform.validator.base;

import com.viadeo.kasper.api.domain.response.KasperReason;

public interface ReturnTypeValidator<VALIDATOR extends ReturnTypeValidator> {

    VALIDATOR expectReturnOK();

    VALIDATOR expectReturnError(KasperReason reason);

    VALIDATOR expectReturnRefused(KasperReason reason);

}
