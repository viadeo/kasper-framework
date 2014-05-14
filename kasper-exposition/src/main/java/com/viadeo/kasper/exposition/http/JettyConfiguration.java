// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

public class JettyConfiguration {
    static final int DEFAULT_ACCEPTORS = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
    static final int DEFAULT_MIN_THREADS = DEFAULT_ACCEPTORS * 2;

    private final Config config;

    public JettyConfiguration(Config config) {
        this.config = config;
    }

    public Config getConfig() {
        return this.config;
    }

    public int getPort() {
        return getInt(this.config, "port");
    }

    public int getAdminPort() {
        return getInt(this.config, "adminPort");
    }

    public String getHost() {
        return getString(this.config, "bindHost");
    }

    public int getAcceptors() {
        return this.getIntOrDefault(this.config, "acceptorThreads", DEFAULT_ACCEPTORS);
    }

    public boolean isForwarded() {
        return getBoolean(this.config, "useForwardedHeaders");
    }

    public int getMaxIdleTime() {
        return getMilliseconds(this.config, "maxIdleTime").intValue();
    }

    public int getLowResourcesMaxIdleTime() {
        return getMilliseconds(this.config, "lowResourcesMaxIdleTime").intValue();
    }

    public int getAcceptorPriorityOffset() {
        return getInt(this.config, "acceptorThreadPriorityOffset");
    }

    public int getAcceptQueueSize() {
        return getInt(this.config, "acceptQueueSize");
    }

    public int getMaxBuffers() {
        return this.getIntOrDefault(this.config, "maxBufferCount", DEFAULT_ACCEPTORS);
    }

    public int getRequestBufferSize() {
        return getBytes(this.config, "requestBufferSize").intValue();
    }

    public int getRequestHeaderSize() {
        return getBytes(this.config, "requestHeaderBufferSize").intValue();
    }

    public int getResponseBufferSize() {
        return getBytes(this.config, "responseBufferSize").intValue();
    }

    public int getResponseHeaderSize() {
        return getBytes(this.config, "responseHeaderBufferSize").intValue();
    }

    public int getPoolMinThreads() {
        return this.getIntOrDefault(this.config, "minThreads", DEFAULT_MIN_THREADS);
    }

    public int getPoolMaxThreads() {
        return getInt(this.config, "maxThreads");
    }

    public int getShutdownGracePeriod() {
        return getMilliseconds(this.config, "shutdownGracePeriod").intValue();
    }

    public String getQueryPath() {
        return getString(this.config, "path.query");
    }

    public String getCommandPath() {
        return getString(this.config, "path.command");
    }

    public String getEventPath() {
        return getString(this.config, "path.event");
    }

    public boolean isJmxEnabled() {
        return getBoolean(this.config, "jmx.enabled");
    }

    private int getIntOrDefault(Config config, String key, int defaultValue) {
        try {
            return getInt(config, key);
        } catch (ConfigException.WrongType e) {
            String value = getString(config, key);
            if ("auto".equals(value)) {
                return defaultValue;
            } else {
                throw new IllegalArgumentException("Fail to load \"" + key + "\" from TypeSafe Config. Allowed values are \"auto\" or a number.");
            }
        }
    }

    private int getInt(Config config, String key) {
        // FIXME this method should not exist https://github.com/typesafehub/config/issues/92
        try {
            return config.getInt(key);
        } catch (ConfigException.Missing e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private String getString(Config config, String key) {
        // FIXME this method should not exist https://github.com/typesafehub/config/issues/92
        try {
            return config.getString(key);
        } catch (ConfigException.Missing e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private boolean getBoolean(Config config, String key) {
        // FIXME this method should not exist https://github.com/typesafehub/config/issues/92
        try {
            return config.getBoolean(key);
        } catch (ConfigException.Missing e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Long getBytes(Config config, String key) {
        // FIXME this method should not exist https://github.com/typesafehub/config/issues/92
        try {
            return config.getBytes(key);
        } catch (ConfigException.Missing e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Long getMilliseconds(Config config, String key) {
        // FIXME this method should not exist https://github.com/typesafehub/config/issues/92
        try {
            return config.getMilliseconds(key);
        } catch (ConfigException.Missing e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}