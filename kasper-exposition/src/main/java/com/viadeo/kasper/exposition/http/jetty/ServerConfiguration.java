// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.jetty;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;

public class ServerConfiguration {

    static final int DEFAULT_ACCEPTORS = Math.max(1, Runtime.getRuntime().availableProcessors() / 2);
    static final int DEFAULT_MIN_THREADS = DEFAULT_ACCEPTORS * 2;

    private final Config config;

    public ServerConfiguration(final Config config) {
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

    public int getMaxQueued() {
        return getInt(this.config, "maxQueued");
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

    private int getIntOrDefault(final Config config, final String key, final int defaultValue) {
        try {
            return getInt(config, key);
        } catch (final ConfigException.WrongType e) {
            final String value = getString(config, key);
            if ("auto".equals(value)) {
                return defaultValue;
            } else {
                throw new IllegalArgumentException("Fail to load \"" + key + "\" from TypeSafe Config. Allowed values are \"auto\" or a number.");
            }
        }
    }

    private int getInt(final Config config, final String key) {
        // FIXME this method should not exist https://github.com/typesafehub/config/issues/92
        try {
            return config.getInt(key);
        } catch (ConfigException.Missing e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private String getString(final Config config, final String key) {
        // FIXME this method should not exist https://github.com/typesafehub/config/issues/92
        try {
            return config.getString(key);
        } catch (ConfigException.Missing e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private boolean getBoolean(final Config config, final String key) {
        // FIXME this method should not exist https://github.com/typesafehub/config/issues/92
        try {
            return config.getBoolean(key);
        } catch (ConfigException.Missing e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Long getBytes(final Config config, final String key) {
        // FIXME this method should not exist https://github.com/typesafehub/config/issues/92
        try {
            return config.getBytes(key);
        } catch (ConfigException.Missing e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private Long getMilliseconds(final Config config, final String key) {
        // FIXME this method should not exist https://github.com/typesafehub/config/issues/92
        try {
            return config.getMilliseconds(key);
        } catch (ConfigException.Missing e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

}
