package com.viadeo.kasper.exposition;

import com.viadeo.kasper.client.platform.Plugin;

public interface HttpExposerPlugin extends Plugin {
    public HttpExposer getHttpExposer();
}