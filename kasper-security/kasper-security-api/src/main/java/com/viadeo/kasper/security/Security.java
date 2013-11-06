package com.viadeo.kasper.security;

import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.annotation.XKasperDomain;

@XKasperDomain(label = "Security", prefix = "sec", description="The kasper security domain")
public class Security implements Domain {
}
