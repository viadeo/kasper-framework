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

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.viadeo.kasper.exposition.http.HttpCommandExposer;
import com.viadeo.kasper.exposition.http.HttpEventExposer;
import com.viadeo.kasper.exposition.http.HttpQueryExposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import javax.ws.rs.core.Application;

public class Server implements SmartLifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final org.eclipse.jetty.server.Server server;

    // ------------------------------------------------------------------------

    public Server(
            final ServerConfiguration config,
            final HttpQueryExposer queryExposer,
            final HttpCommandExposer commandExposer,
            final HttpEventExposer eventExposer,
            final Application application,
            final HealthCheckRegistry healthCheckRegistry,
            final MetricRegistry metricRegistry
    ) {
        this.server = new ServerBuilder(config)
                .withQueryExposer(queryExposer)
                .withCommandExposer(commandExposer)
                .withEventExposer(eventExposer)
                .withJaxRs(application)
                .withHealthCheckRegistry(healthCheckRegistry)
                .withMetricRegistry(metricRegistry)
                .build();
    }

    // ------------------------------------------------------------------------

    public void start() {
        try {
            this.server.start();
            LOGGER.info("Http server started");
        } catch (Exception e) {
            LOGGER.error("Fail to start the Jetty server... please check the availability of the port.");
            throw Throwables.propagate(e);
        }
    }

    public void stop() {
        Preconditions.checkState(server.isStarted(), "Server was not started, please call start before stop");
        try {
            this.server.stop();
        } catch (final Exception e) {
            LOGGER.error("An error occured while stopping the Jetty server", e);
            throw Throwables.propagate(e);
        }
    }

    @Override
    public boolean isRunning() {
        return this.server.isRunning();
    }

    public int getPort() {
        return ServerBuilder.getPort(this.server);
    }

    public int getAdminPort() {
        return ServerBuilder.getAdminPort(this.server);
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    public void start(final Runnable callback) {
        start();
        callback.run();
    }

    @Override
    public void stop(final Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

}
