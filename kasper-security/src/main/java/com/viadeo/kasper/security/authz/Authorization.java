package com.viadeo.kasper.security.authz;

import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;

@XKasperDomain (
        label = "Authorization",
        prefix = "authz",
        description = "The Authorization domain"
)
public class Authorization implements Domain {


}

