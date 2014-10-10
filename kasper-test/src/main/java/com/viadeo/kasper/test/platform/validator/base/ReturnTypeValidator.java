package com.viadeo.kasper.test.platform.validator.base;

import com.viadeo.kasper.KasperReason;

public interface ReturnTypeValidator<VALIDATOR extends ReturnTypeValidator> {

    VALIDATOR expectReturnOK();

    VALIDATOR expectReturnError(KasperReason reason);

    VALIDATOR expectReturnRefused(KasperReason reason);

}
