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

import com.google.common.base.Optional;
import com.typesafe.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;

/*
 * Resolve a Kasper configuration layout
 *
 * Levels of priority of configuration assets ( L(n) overrides keys of L(n+1) ) :
 *
 * L1- APPLICATION ENVIRONMENT        -> functional configuration by environment (kasper_env)
 * L2- DOMAINS ENVIRONMENT            -> domains functional configuration by environment (kasper_env)*
 * L3- SYSTEM INFRASTRUCTURE          -> system configuration MANAED BY OPS (deployed on the machine)
 * L4- APPLICATION INFRASTRUCTURE     -> base infrastructure configuration*
 * L5- APPLICATION (PLATFORM)         -> functional configuration for the whole platform
 * L6- DOMAINS INFRASTRUCTURE         -> domains infrastructure configuration
 * L7- DOMAINS                        -> domains functional configuration
 *
 * Default expected locations and keys hierarchies (roots) :
 *
 * L1- APPLICATION ENVIRONMENT
 *  - location: kplatform-web/src/config/environments/<kasper_env>.conf
 *  - keys hierarchy: 'runtime', 'kasper', 'application'
 *
 * L2- DOMAINS ENVIRONMENT
 *  - location: kplatform-<domain>-[command|query]/src/config/environments/<kasper_env>.conf
 *  - keys hierarchy: '<domain>'
 *
 * L3- SYSTEM INFRASTRUCTURE
 *  - location: infrastructure.conf
 *  - keys hierarchy: 'system.infrastructure'
 *
 * L4- APPLICATION INFRASTRUCTURE (OPTIONAL MAP FROM SYSTEM INFRASTRUCTURE)
 *  - location; kplatform-web/src/config/infrastructure/infrastructure.conf
 *  - keys hierarchy: 'infrastructure' overrides 'infrastructure.default'
 *
 * L5- APPLICATION
 *  - location: kplatform-web/src/config/platform.conf
 *  - keys hierarchy: 'runtime', 'kasper'
 *
 * L6- DOMAINS INFRASTRUCTURE (OPTIONAL MAP FROM SYSTEM INFRASTRUCTURE)
 *  - location: kplatform-<domain>-[command|query]/src/config/infrastructure/infrastructure.conf
 *  - keys hierarchy: '<domain>.infrastructure' overrides '<domain>.infrastructure.default'
 *
 * L7- DOMAINS
 *  - location: kplatform-<domain>-[command|query]/src/config/domain.conf
 *  - keys hierarchy: '<domain>'
 *
 */
public class ConfigurationLoader {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLoader.class);

    private static final String DEFAULT_ENVIRONMENT_NAME = "KASPER_ENV";

    private static final String DEFAULT_SYSTEM_FILENAME = "infrastructure.conf";

    private static final String DEFAULT_APP_CONFIG_PREFIX = "config";
    private static final String APP_CONFIG_NAME = "platform";

    private static final String DEFAULT_DOMAINS_CONFIG_PREFIX = "config";
    private static final String DOMAINS_CONFIG_NAME = "domain";

    private static final String INFRASTRUCTURE_CONFIG_NAME = "infrastructure";
    private static final String ENVIRONMENT_CONFIG_NAME = "environments";

    private final Options options;

    private Config globalConfiguration;


    // ------------------------------------------------------------------------

    public static class Options {

        protected String domainsConfPrefix;
        protected String applicationConfPrefix;
        protected String environmentName;
        protected String systemFileName;
        protected String forcedEnvironment;

        // - Build ----------------------------------------

        private Options() {
        }

        public static Options defaults() {
            return new Options() {{
                this.environmentName = DEFAULT_ENVIRONMENT_NAME;
                this.systemFileName = DEFAULT_SYSTEM_FILENAME;
                this.applicationConfPrefix = DEFAULT_APP_CONFIG_PREFIX;
                this.domainsConfPrefix = DEFAULT_DOMAINS_CONFIG_PREFIX;
                this.forcedEnvironment = null;
            }};
        }

        // - Getters --------------------------------------

        public String domainsConfPrefix() {
            return this.domainsConfPrefix;
        }

        public String applicationConfPrefix() {
            return this.applicationConfPrefix;
        }

        public String environmentName() {
            return environmentName;
        }

        public String systemFileName() {
            return systemFileName;
        }

        public Optional<String> environment() {
            if (null != this.forcedEnvironment) {
                return Optional.of(this.forcedEnvironment);
            }
            return Optional.fromNullable(System.getenv(environmentName));
        }

        // - Setters --------------------------------------

        public Options domainsConfPrefix(final String prefix) {
            this.domainsConfPrefix = prefix;
            return this;
        }

        public Options applicationConfPrefix(final String prefix) {
            this.applicationConfPrefix = prefix;
            return this;
        }


        public Options systemFileName(final String filename) {
            this.systemFileName = filename;
            return this;
        }

        public Options forcedEnvironment(final String forcedEnvironment) {
            this.forcedEnvironment = forcedEnvironment;
            return this;
        }

    }

    public ConfigurationLoader(final Options options) {
        this.options = options;
    }

    public Config getConfiguration() {
        if (null != globalConfiguration) {
            return globalConfiguration;
        }
        return ConfigFactory.empty();
    }

    // ------------------------------------------------------------------------

    public Config load() {
        LOGGER.info("** Resolve Kasper configuration...");

        /* Load system infrastructure configuration */
        final Config systemInfrastructureConfig = getSystemInfrastructureConfig();

        /* Application config */
        final Config appInfrastructureConfig =
                getApplicationInfrastructureConfig(options.applicationConfPrefix());
        final Config appEnvironmentConfig =
                getEnvironmentConfig(options.applicationConfPrefix());
        final Config appConfig =
                getApplicationConfig(options.applicationConfPrefix, APP_CONFIG_NAME);

        /* Domains config */
        Config domainsInfrastructureConfig = ConfigFactory.empty();
        Config domainsEnvironmentConfig = ConfigFactory.empty();
        if (!options.applicationConfPrefix().equals(options.domainsConfPrefix())) {
            domainsInfrastructureConfig =
                    getApplicationInfrastructureConfig(options.domainsConfPrefix());
            domainsEnvironmentConfig =
                    getEnvironmentConfig(options.domainsConfPrefix());
        }
        final Config domainsConfig =
                getApplicationConfig(options.domainsConfPrefix, DOMAINS_CONFIG_NAME);

        /* ===== Compute a global configuration applying overwrite rules ===== */
        globalConfiguration =
                /* L1- APPLICATION ENVIRONMENT */
                appEnvironmentConfig
                    /* L2- DOMAINS ENVIRONEMNT */
                    .withFallback(domainsEnvironmentConfig
                        /* L3- SYSTEM INFRASTRUCTURE */
                        .withFallback(systemInfrastructureConfig
                            /* L4- APPLICATION INFRASTRUCTURE */
                            .withFallback(appInfrastructureConfig
                                /* L5- APPLICATION */
                                .withFallback(appConfig
                                     /* L6- DOMAINS INFRASTRUCTURE */
                                     .withFallback(domainsInfrastructureConfig
                                        /* L7- DOMAINS */
                                        .withFallback(domainsConfig)
                                     )
                                )
                            )
                        )
                    );


        return globalConfiguration;
    }

    // ------------------------------------------------------------------------

    /*
     * System infrastructure file
     */
    private Config getSystemInfrastructureConfig() {
        final File systemInfrastructureFile = new File(options.systemFileName());

        Config systemInfrastructureConfig = null;
        if (systemInfrastructureFile.exists()) {
            LOGGER.debug("Loading system configuration file from " + systemInfrastructureFile);
            systemInfrastructureConfig = ConfigFactory.parseFile(
                    systemInfrastructureFile,
                    ConfigParseOptions.defaults().setAllowMissing(true)
            );
        } else {
            LOGGER.debug(String.format("No system configuration found (%s)", systemInfrastructureFile));
        }

        return foundOrEmpty(systemInfrastructureConfig);
    }

    /*
     * Loads application-defined infrastructure configuration
     */
    public Config getApplicationInfrastructureConfig(final String appPrefix) {
        LOGGER.info("Loading app infrastructure from prefix " + appPrefix);

        /*
         * Infrastructure files (app and domains) can use system placeholders
         */
        final Config appInfrastructureConfig = ConfigFactory.load(
                appPrefix
                        + "/" + INFRASTRUCTURE_CONFIG_NAME,
                ConfigParseOptions.defaults().setAllowMissing(true),
                ConfigResolveOptions.defaults()
        );

        return foundOrEmpty(appInfrastructureConfig);
    }

    /*
     * Load environment-specific application config from classpath
     */
    public Config getEnvironmentConfig(final String appPrefix) {
        LOGGER.debug("Loading app environment from prefix " + appPrefix);

        Config appEnvironmentConfig = null;
        final Optional<String> environment = options.environment();
        if (environment.isPresent()) {
            appEnvironmentConfig = ConfigFactory.load(
                    appPrefix
                            + "/" + ENVIRONMENT_CONFIG_NAME
                            + "/" + environment.get(),
                    ConfigParseOptions.defaults().setAllowMissing(true),
                    ConfigResolveOptions.defaults()
            );
        } else {
            LOGGER.debug(String.format("  -> No specific environment is set (system var %s)",
                    options.environmentName()));
        }

        /* Warns about infrastructure overriding in environment */
        if (null != appEnvironmentConfig) {
            if (appEnvironmentConfig.hasPath(INFRASTRUCTURE_CONFIG_NAME)) {
                LOGGER.warn("Environment overrides system infrastructure : ",
                        appEnvironmentConfig.getConfig(INFRASTRUCTURE_CONFIG_NAME).root().render());
            }
            for (final Map.Entry<String, ConfigValue> entry : appEnvironmentConfig.entrySet()) {
                if (appEnvironmentConfig.hasPath(entry.getKey() + "." + INFRASTRUCTURE_CONFIG_NAME)) {
                    LOGGER.warn("Environment overrides system infrastructure in " + entry.getKey() + " :",
                            appEnvironmentConfig.getConfig(INFRASTRUCTURE_CONFIG_NAME).root().render());
                }
            }
        }

        return foundOrEmpty(appEnvironmentConfig);
    }

    /*
     * Load common application config from classpath
     */
    public Config getApplicationConfig(final String appPrefix, final String appName) {
        LOGGER.debug("Loading app configuration from prefix " + appPrefix + " for name " + appName);
        return ConfigFactory.load(appPrefix + "/" + appName);
    }

    // ------------------------------------------------------------------------

    private Config foundOrEmpty(final Config config) {
        if (null != config) {
            return config;
        }
        return ConfigFactory.empty();
    }

}
