package com.viadeo.kasper.client.platform;

import com.viadeo.kasper.client.platform.domain.descriptor.PlatformDescriptor;

public interface Plugin {

    void initialize(PlatformDescriptor platformDescriptor);

}
