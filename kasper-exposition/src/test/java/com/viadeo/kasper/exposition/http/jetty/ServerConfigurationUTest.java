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

import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ServerConfigurationUTest {
    @Test
    public void getAcceptors_auto() {
        ServerConfiguration conf = new ServerConfiguration(ConfigFactory.empty().withValue("acceptorThreads", ConfigValueFactory.fromAnyRef("auto")));
        assertEquals(ServerConfiguration.DEFAULT_ACCEPTORS, conf.getAcceptors());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAcceptors_with_bad_string_throws_exception() {
        ServerConfiguration conf = new ServerConfiguration(ConfigFactory.empty().withValue("acceptorThreads", ConfigValueFactory.fromAnyRef("bad-string")));
        conf.getAcceptors();
    }

    @Test
    public void getMaxBuffers_auto() {
        ServerConfiguration conf = new ServerConfiguration(ConfigFactory.empty().withValue("maxBufferCount", ConfigValueFactory.fromAnyRef("auto")));
        assertEquals(ServerConfiguration.DEFAULT_ACCEPTORS, conf.getMaxBuffers());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getMaxBuffers_with_bad_string_throws_exception() {
        ServerConfiguration conf = new ServerConfiguration(ConfigFactory.empty().withValue("maxBufferCount", ConfigValueFactory.fromAnyRef("bad-string")));
        conf.getMaxBuffers();
    }

    @Test
    public void getPoolMinThreads_auto() {
        ServerConfiguration conf = new ServerConfiguration(ConfigFactory.empty().withValue("minThreads", ConfigValueFactory.fromAnyRef("auto")));
        assertEquals(ServerConfiguration.DEFAULT_MIN_THREADS, conf.getPoolMinThreads());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPoolMinThreads_with_bad_string_throws_exception() {
        ServerConfiguration conf = new ServerConfiguration(ConfigFactory.empty().withValue("minThreads", ConfigValueFactory.fromAnyRef("bad-string")));
        conf.getPoolMinThreads();
    }
}
