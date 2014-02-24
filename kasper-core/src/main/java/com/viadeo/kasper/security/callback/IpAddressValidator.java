package com.viadeo.kasper.security.callback;

import com.viadeo.kasper.security.KasperInvalidApplicationIdException;
import com.viadeo.kasper.security.KasperInvalidIpAddressException;
import com.viadeo.kasper.security.KasperMissingApplicationIdException;
import com.viadeo.kasper.security.KasperMissingIpAddressException;


/**
 * Capability to validate a ipAddress.
 */
public interface IpAddressValidator {

    void validate(String ipAddress) throws KasperMissingIpAddressException, KasperInvalidIpAddressException;

}
