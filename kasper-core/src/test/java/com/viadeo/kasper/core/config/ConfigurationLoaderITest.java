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
//           Kasper Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.config;

import com.typesafe.config.Config;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class ConfigurationLoaderITest {

    private static final String MAIN_PREFIX = "main/config";
    private static final String DOMAINS_PREFIX = "domain/config";
    private static final String SYSTEM_FILE_CLASSPATH = "system/etc/viadeo/viadeo-platform/infrastructure.conf";

    // ------------------------------------------------------------------------

    private static File systemFile;

    private File getSystemFile() throws IOException {
        if (null == systemFile) {
            systemFile = Files.createTempFile("x", "x").toFile();

            final OutputStream systemOutputStream = new FileOutputStream(systemFile);
            final InputStream systemInputStream = this.getClass().getClassLoader().getResourceAsStream(SYSTEM_FILE_CLASSPATH);
            if (null != systemInputStream) {
                int len;
                final byte[] buffer = new byte[64];
                while ((len = systemInputStream.read(buffer)) != -1) {
                    systemOutputStream.write(buffer, 0, len);
                }

                systemInputStream.close();
                systemOutputStream.close();
            } else {
                throw new RuntimeException("Unable to create temporary system file");
            }
        }
        return systemFile;
    }

    @AfterClass
    public static void afterClass() {
        if (null != systemFile) {
            systemFile.delete();
        }
    }

    // ------------------------------------------------------------------------
    // APPLICATION ONLY
    // ------------------------------------------------------------------------

    @Test
    public void test_MainApplicationOnly_noEnv_noSystem() throws Exception {
        // Given
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .applicationConfPrefix(MAIN_PREFIX)
                        .forcedEnvironment("")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals(42, c.getInt("runtime.middleware.timeout"));
        assertEquals("myTrigger", c.getString("runtime.middleware.trigger"));
        assertEquals("dev.middleware.com", c.getString("infrastructure.middleware.host"));
        assertEquals(4242, c.getInt("infrastructure.mysql.port"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_MainApplicationOnly_noEnv_withSystem() throws Exception {
        // Given
        final File tmpSystemFile = getSystemFile();
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .applicationConfPrefix(MAIN_PREFIX)
                        .systemFileName(tmpSystemFile.getAbsolutePath())
                        .forcedEnvironment("")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals(42, c.getInt("runtime.middleware.timeout"));
        assertEquals("myTrigger", c.getString("runtime.middleware.trigger"));
        assertEquals("prod.middleware.com", c.getString("infrastructure.middleware.host"));
        assertEquals(3306, c.getInt("infrastructure.mysql.port"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_MainApplicationOnly_withEnv_noSystem() throws Exception {
        // Given
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .applicationConfPrefix(MAIN_PREFIX)
                        .forcedEnvironment("test")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals(42, c.getInt("runtime.middleware.timeout"));
        assertEquals("myTestTrigger", c.getString("runtime.middleware.trigger"));
        assertEquals("dev.middleware.com", c.getString("infrastructure.middleware.host"));
        assertEquals(4567, c.getInt("infrastructure.mysql.port"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_MainApplicationOnly_withEnv_withSystem() throws Exception {
        // Given
        final File tmpSystemFile = getSystemFile();
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .applicationConfPrefix(MAIN_PREFIX)
                        .systemFileName(tmpSystemFile.getAbsolutePath())
                        .forcedEnvironment("test")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals(42, c.getInt("runtime.middleware.timeout"));
        assertEquals("myTestTrigger", c.getString("runtime.middleware.trigger"));
        assertEquals("prod.middleware.com", c.getString("infrastructure.middleware.host"));
        assertEquals(4567, c.getInt("infrastructure.mysql.port"));
    }

    // ------------------------------------------------------------------------
    // DOMAINS ONLY
    // ------------------------------------------------------------------------

    @Test
    public void test_DomainApplicationOnly_noEnv_noSystem() throws Exception {
        // Given
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .domainsConfPrefix(DOMAINS_PREFIX)
                        .forcedEnvironment("")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals("MyApiKey", c.getString("myDomain.application.partner.api_key"));
        assertEquals("/theirResource/v2/", c.getString("myDomain.application.partner.endpoint"));
        assertEquals("partner.host.com", c.getString("myDomain.infrastructure.partner.host"));
        assertEquals(4242, c.getInt("myDomain.infrastructure.partner.port"));
        assertEquals("dev.webservice.com", c.getString("myDomain.infrastructure.webservice.host"));
        assertEquals(1234, c.getInt("infrastructure.mysql.port"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_DomainApplicationOnly_noEnv_withSystem() throws Exception {
        // Given
        final File tmpSystemFile = getSystemFile();
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .domainsConfPrefix(DOMAINS_PREFIX)
                        .systemFileName(tmpSystemFile.getAbsolutePath())
                        .forcedEnvironment("")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals("MyApiKey", c.getString("myDomain.application.partner.api_key"));
        assertEquals("/theirResource/v2/", c.getString("myDomain.application.partner.endpoint"));
        assertEquals("partner.host.com", c.getString("myDomain.infrastructure.partner.host"));
        assertEquals(4242, c.getInt("myDomain.infrastructure.partner.port"));
        assertEquals("prod.webservice.com", c.getString("myDomain.infrastructure.webservice.host"));
        assertEquals(3306, c.getInt("infrastructure.mysql.port"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_DomainApplicationOnly_withEnv_noSystem() throws Exception {
        // Given
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .domainsConfPrefix(DOMAINS_PREFIX)
                        .forcedEnvironment("test")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals("MyTestApiKey", c.getString("myDomain.application.partner.api_key"));
        assertEquals("/theirResource/v2/", c.getString("myDomain.application.partner.endpoint"));
        assertEquals("test.partner.host.com", c.getString("myDomain.infrastructure.partner.host"));
        assertEquals(4242, c.getInt("myDomain.infrastructure.partner.port"));
        assertEquals("dev.webservice.com", c.getString("myDomain.infrastructure.webservice.host"));
        assertEquals(6789, c.getInt("infrastructure.mysql.port"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_DomainApplicationOnly_withEnv_withSystem() throws Exception {
        // Given
        final File tmpSystemFile = getSystemFile();
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .domainsConfPrefix(DOMAINS_PREFIX)
                        .systemFileName(tmpSystemFile.getAbsolutePath())
                        .forcedEnvironment("test")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals("MyTestApiKey", c.getString("myDomain.application.partner.api_key"));
        assertEquals("/theirResource/v2/", c.getString("myDomain.application.partner.endpoint"));
        assertEquals("test.partner.host.com", c.getString("myDomain.infrastructure.partner.host"));
        assertEquals(4242, c.getInt("myDomain.infrastructure.partner.port"));
        assertEquals("prod.webservice.com", c.getString("myDomain.infrastructure.webservice.host"));
        assertEquals(6789, c.getInt("infrastructure.mysql.port"));
    }

    // ------------------------------------------------------------------------
    // GLOBAL APPLICATION
    // ------------------------------------------------------------------------

    @Test
    public void test_Global_noEnv_noSystem() throws Exception {
        // Given
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .domainsConfPrefix(DOMAINS_PREFIX)
                        .applicationConfPrefix(MAIN_PREFIX)
                        .forcedEnvironment("")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals("MyApiKey", c.getString("myDomain.application.partner.api_key"));
        assertEquals("/theirResource/v2/", c.getString("myDomain.application.partner.endpoint"));
        assertEquals("partner.host.com", c.getString("myDomain.infrastructure.partner.host"));
        assertEquals(4242, c.getInt("myDomain.infrastructure.partner.port"));
        assertEquals("dev.webservice.com", c.getString("myDomain.infrastructure.webservice.host"));
        assertEquals(4242, c.getInt("infrastructure.mysql.port"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_Global_noEnv_withSystem() throws Exception {
        // Given
        final File tmpSystemFile = getSystemFile();
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .domainsConfPrefix(DOMAINS_PREFIX)
                        .applicationConfPrefix(MAIN_PREFIX)
                        .systemFileName(tmpSystemFile.getAbsolutePath())
                        .forcedEnvironment("")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals("MyApiKey", c.getString("myDomain.application.partner.api_key"));
        assertEquals("/theirResource/v2/", c.getString("myDomain.application.partner.endpoint"));
        assertEquals("partner.host.com", c.getString("myDomain.infrastructure.partner.host"));
        assertEquals(4242, c.getInt("myDomain.infrastructure.partner.port"));
        assertEquals("prod.webservice.com", c.getString("myDomain.infrastructure.webservice.host"));
        assertEquals(3306, c.getInt("infrastructure.mysql.port"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_Global_withEnv_noSystem() throws Exception {
        // Given
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .domainsConfPrefix(DOMAINS_PREFIX)
                        .applicationConfPrefix(MAIN_PREFIX)
                        .forcedEnvironment("test")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals("MyTestApiKey", c.getString("myDomain.application.partner.api_key"));
        assertEquals("/theirResource/v2/", c.getString("myDomain.application.partner.endpoint"));
        assertEquals("test.partner.host.com", c.getString("myDomain.infrastructure.partner.host"));
        assertEquals(4242, c.getInt("myDomain.infrastructure.partner.port"));
        assertEquals("dev.webservice.com", c.getString("myDomain.infrastructure.webservice.host"));
        assertEquals(4567, c.getInt("infrastructure.mysql.port"));
    }

    // ------------------------------------------------------------------------

    @Test
    public void test_Global_withEnv_withSystem() throws Exception {
        // Given
        final File tmpSystemFile = getSystemFile();
        final ConfigurationLoader config = new ConfigurationLoader(
                ConfigurationLoader.Options.defaults()
                        .domainsConfPrefix(DOMAINS_PREFIX)
                        .applicationConfPrefix(MAIN_PREFIX)
                        .systemFileName(tmpSystemFile.getAbsolutePath())
                        .forcedEnvironment("test")
        );

        // when
        config.load();

        // When
        final Config c = config.getConfiguration();
        assertEquals("MyTestApiKey", c.getString("myDomain.application.partner.api_key"));
        assertEquals("/theirResource/v2/", c.getString("myDomain.application.partner.endpoint"));
        assertEquals("test.partner.host.com", c.getString("myDomain.infrastructure.partner.host"));
        assertEquals(4242, c.getInt("myDomain.infrastructure.partner.port"));
        assertEquals("prod.webservice.com", c.getString("myDomain.infrastructure.webservice.host"));
        assertEquals(4567, c.getInt("infrastructure.mysql.port"));
    }

}
